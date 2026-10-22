package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.exception.ResumeException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import com.bdis.bdis_website.dto.ResumeFormDTO;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ResumeDocumentService {
    private final ResumeProperties properties;
    private final ResourceLoader resources;
    private final Clock clock;

    public ResumeDocumentService(ResumeProperties properties, ResourceLoader resources, Clock resumeClock) {
        this.properties = properties;
        this.resources = resources;
        this.clock = resumeClock.withZone(ZoneId.of("Asia/Shanghai"));
    }

    public String currentAcademicYear() {
        return academicYear(LocalDate.now(clock));
    }

    public static String academicYear(LocalDate date) {
        int start = date.getMonthValue() >= 9 ? date.getYear() : date.getYear() - 1;
        return start + "-" + (start + 1);
    }

    public String documentName(String name, String studentNo, String academicYear) {
        String result = safeName(name) + "-" + safeName(studentNo) + "-" + yearName(academicYear)
                + "大数据智能工作室招新报名表";
        if ((result + ".docx").getBytes(StandardCharsets.UTF_8).length > 240) {
            throw new ResumeException(400, "姓名或学号过长，无法生成兼容的文件名，请检查填写内容");
        }
        return result;
    }

    public String blankFilename() {
        return yearName(currentAcademicYear()) + "大数据智能工作室招新报名表-空白模板.docx";
    }

    private String yearName(String year) {
        return year.replace("-", "学年-") + "学年";
    }

    private String safeName(String value) {
        String result = value.strip().replaceAll("[\\p{Cntrl}\\\\/:*?\"<>|]", "_")
                .replaceAll("[. ]+$", "");
        if (result.isBlank()) {
            throw new ResumeException(400, "姓名和学号不能为空");
        }
        return result;
    }

    public byte[] blankTemplate() {
        try (var input = resources.getResource(properties.getTemplate()).getInputStream();
             var doc = new XWPFDocument(input);
             var output = new ByteArrayOutputStream()) {
            replaceTitle(doc, currentAcademicYear());
            doc.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new ResumeException(503, "简历模板暂不可用，请稍后重试");
        }
    }

    public void generate(ResumeFormDTO form, String year, byte[] photo, Path output) throws IOException {
        try (var input = resources.getResource(properties.getTemplate()).getInputStream();
             var doc = new XWPFDocument(input)) {
            replaceTitle(doc, year);
            XWPFTable first = doc.getTables().get(0);
            first.getRow(6).setHeight(9000);
            setCell(first, 1, 1, form.getName(), false);
            setCell(first, 1, 3, form.getGender(), false);
            setCell(first, 1, 5, form.getEthnicity(), false);
            setCell(first, 2, 1, form.getCollege(), false);
            setCell(first, 2, 3, form.getStudentNo(), false);
            setCell(first, 2, 5, form.getBirthMonth(), false);
            setCell(first, 3, 1, form.getMajor(), false);
            setCell(first, 3, 3, form.getPhone(), false);
            setCell(first, 4, 1, form.getSpecialties(), false);
            setCell(first, 4, 3, form.getGpa(), false);
            setCell(first, 5, 1, Boolean.TRUE.equals(form.getHasCompetitionExperience()) ? "是" : "否", false);
            setCell(first, 6, 1, form.getIntro(), true);
            setCell(doc.getTables().get(1), 0, 1, form.getAwards(), true);
            setCell(doc.getTables().get(1), 1, 1, form.getShowcase(), true);
            if (photo != null) {
                XWPFTableCell cell = first.getRow(1).getCell(6);
                XWPFParagraph paragraph = cell.getParagraphs().get(0);
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = paragraph.createRun();
                try {
                    run.addPicture(new java.io.ByteArrayInputStream(photo), Document.PICTURE_TYPE_PNG,
                            "证件照.png", Units.toEMU(66), Units.toEMU(99));
                } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
                    throw new ResumeException(400, "照片无法合成，请更换图片");
                }
            }
            try (var stream = Files.newOutputStream(output)) { doc.write(stream); }
        }
    }

    public byte[] validateUpload(org.springframework.web.multipart.MultipartFile upload) {
        if (upload == null || upload.isEmpty()) throw new ResumeException(400, "请选择已填写的Word文档");
        if (upload.getSize() > 10L * 1024 * 1024) throw new ResumeException(413, "Word文档不能超过10MB");
        String name = upload.getOriginalFilename();
        if (name == null || !name.toLowerCase(java.util.Locale.ROOT).endsWith(".docx"))
            throw new ResumeException(400, "仅支持DOCX文件，请使用Word或WPS另存为.docx");
        try {
            byte[] bytes = upload.getBytes();
            boolean documentFound = false;
            boolean typesFound = false;
            long total = 0;
            int entries = 0;
            try (var zip = new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes))) {
                java.util.zip.ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    String part = entry.getName().toLowerCase(java.util.Locale.ROOT);
                    if (++entries > 1000 || part.contains("../") || part.contains("\\") || part.startsWith("/")
                            || part.contains("vbaproject") || part.contains("/activex/") || part.contains("/embeddings/"))
                        throw new ResumeException(400, "Word包含不支持的嵌入对象、宏或异常结构，请另存为普通DOCX");
                    byte[] data = zip.readNBytes(20 * 1024 * 1024 + 1);
                    total += data.length;
                    if (data.length > 20 * 1024 * 1024 || total > 60L * 1024 * 1024)
                        throw new ResumeException(400, "Word解压后过大，请压缩图片并减少内容后上传");
                    if (part.equals("word/document.xml")) documentFound = true;
                    if (part.equals("[content_types].xml")) {
                        typesFound = true;
                        if (new String(data, StandardCharsets.UTF_8).toLowerCase(java.util.Locale.ROOT).contains("macroenabled"))
                            throw new ResumeException(400, "不支持包含宏的Word文档");
                    }
                    if (part.endsWith(".rels")) validateRelationships(data);
                }
            }
            if (!documentFound || !typesFound) throw new ResumeException(400, "文件不是有效的DOCX文档");
            try (var document = new XWPFDocument(new java.io.ByteArrayInputStream(bytes))) {
                if (document.getDocument().getBody() == null) throw new ResumeException(400, "Word正文不存在");
            }
            return bytes;
        } catch (ResumeException e) {
            throw e;
        } catch (Exception e) {
            throw new ResumeException(400, "Word文件损坏、已加密或格式不受支持，请另存为DOCX后上传");
        }
    }

    private void validateRelationships(byte[] xml) throws Exception {
        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        var document = factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
        var relationships = document.getElementsByTagNameNS("*", "Relationship");
        for (int i = 0; i < relationships.getLength(); i++) {
            var relationship = (org.w3c.dom.Element) relationships.item(i);
            if (!"External".equalsIgnoreCase(relationship.getAttribute("TargetMode"))) continue;
            String type = relationship.getAttribute("Type");
            String target = relationship.getAttribute("Target").toLowerCase(java.util.Locale.ROOT);
            if (!type.endsWith("/hyperlink") || !(target.startsWith("https://") || target.startsWith("http://") || target.startsWith("mailto:")))
                throw new ResumeException(400, "Word包含外部资源，请将照片直接嵌入文档后重新上传");
        }
    }

    private void setCell(XWPFTable table, int row, int column, String text, boolean narrative) {
        XWPFTableCell cell = table.getRow(row).getCell(column);
        while (cell.getParagraphs().size() > 1) cell.removeParagraph(1);
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        while (!paragraph.getRuns().isEmpty()) paragraph.removeRun(0);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Noto Serif CJK SC");
        run.setFontFamily("Noto Serif CJK SC", XWPFRun.FontCharRange.eastAsia);
        run.setFontSize(12);
        String[] lines = (text == null ? "" : text.strip()).split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) run.addBreak();
            run.setText(lines[i]);
        }
        if (narrative) cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);
    }

    private void replaceTitle(XWPFDocument doc, String year) {
        for (XWPFTable table : doc.getTables()) {
            table.getRows().forEach(row -> row.getTableCells().forEach(cell ->
                    replaceTitle(cell.getParagraphs(), year)));
        }
        replaceTitle(doc.getParagraphs(), year);
    }

    private void replaceTitle(List<XWPFParagraph> paragraphs, String year) {
        for (XWPFParagraph paragraph : paragraphs) {
            if (paragraph.getText().contains("202X-202X")) {
                String text = paragraph.getText().replace("202X-202X", year);
                List<XWPFRun> runs = paragraph.getRuns();
                if (!runs.isEmpty()) {
                    runs.get(0).setText(text, 0);
                    for (int i = runs.size() - 1; i > 0; i--) {
                        paragraph.removeRun(i);
                    }
                }
            }
        }
    }
}
