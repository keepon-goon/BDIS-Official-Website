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
