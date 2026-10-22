package com.bdis.bdis_website.service.impl;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.model.ResumePreviewSnapshot;
import com.bdis.bdis_website.service.*;
import com.bdis.bdis_website.vo.ResumePreviewVO;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import com.bdis.bdis_website.exception.ResumeException;
import java.io.IOException;
import com.bdis.bdis_website.entity.ResumeSubmission;
import com.bdis.bdis_website.mapper.ResumeSubmissionMapper;
import com.bdis.bdis_website.mapper.ResumeSettingsMapper;
import com.bdis.bdis_website.vo.ResumeSubmitVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeStorageService storage;
    private final ResumeDocumentService documents;
    private final ResumeConversionService conversions;
    private final ResumeProperties properties;
    private final Clock clock;
    private final ResumePhotoService photos;
    private final Object stateLock = new Object();
    private final java.util.Set<String> replacing = new java.util.HashSet<>();
    private final ResumeSubmissionMapper submissions;
    private final ResumeSettingsMapper settings;
    private final TransactionTemplate transactions;
    private final ResumeMailService mail;

    public ResumeServiceImpl(ResumeStorageService storage, ResumeDocumentService documents,
                             ResumeConversionService conversions, ResumeProperties properties, Clock resumeClock,
                             ResumePhotoService photos, ResumeSubmissionMapper submissions,
                             ResumeSettingsMapper settings, TransactionTemplate transactions, ResumeMailService mail) {
        this.storage = storage;
        this.documents = documents;
        this.conversions = conversions;
        this.properties = properties;
        this.clock = resumeClock;
        this.photos = photos;
        this.submissions = submissions;
        this.settings = settings;
        this.transactions = transactions;
        this.mail = mail;
    }

    @Override
    public ResumePreviewVO preview(ResumeFormDTO form) {
        return conversions.execute(() -> {
            String token = storage.newToken();
            String hash = storage.hash(token);
            Path work = storage.directory("work", hash);
            Files.createDirectory(work);
            try {
                ResumePreviewSnapshot snapshot = new ResumePreviewSnapshot();
                snapshot.setTokenHash(hash);
                snapshot.setSourceType("FORM");
                snapshot.setForm(form);
                snapshot.setAcademicYear(documents.currentAcademicYear());
                snapshot.setDocumentName(documents.documentName(form.getName(), form.getStudentNo(), snapshot.getAcademicYear()));
                snapshot.setTemplateVersion(properties.getTemplateVersion());
                snapshot.setExpiresAt(clock.instant().plusSeconds(properties.getPreviewMinutes() * 60L));
                Path word = storage.file(work, snapshot.getDocumentName() + ".docx");
                documents.generate(form, snapshot.getAcademicYear(), null, word);
                conversions.convert(word);
                storage.publish(work, snapshot);
                return view(token, snapshot);
            } finally {
                storage.deleteTemporary(work);
            }
        });
    }

    @Override
    public ResumeFile previewFile(String token, boolean pdf) {
        if (confirmed(storage.hash(token)) != null) throw new ResumeException(410, "该预览已确认保存，不能继续读取或修改临时文件");
        var snapshot = storage.load(token);
        String filename = snapshot.getDocumentName() + (pdf ? ".pdf" : ".docx");
        try {
            Path path = storage.file(storage.directory("previews", snapshot.getTokenHash()), filename);
            if (!Files.isRegularFile(path)) throw new ResumeException(404, "预览文件不存在，请重新生成");
            return new ResumeFile(Files.readAllBytes(path), filename);
        } catch (IOException e) {
            throw new ResumeException(503, "预览文件暂不可读，请稍后重试");
        }
    }

    @Override
    public ResumePreviewVO photo(String token, org.springframework.web.multipart.MultipartFile photo) {
        if (photo.isEmpty()) throw new ResumeException(400, "请选择照片");
        if (photo.getSize() > ResumePhotoService.MAX_BYTES) throw new ResumeException(413, "单张照片不能超过5MB");
        String oldHash = storage.hash(token);
        ResumePreviewSnapshot original;
        synchronized (stateLock) {
            original = storage.load(token);
            if (confirmed(oldHash) != null) throw new ResumeException(410, "该预览已确认保存，不能修改正式文件");
            if (!"FORM".equals(original.getSourceType())) throw new ResumeException(400, "请在本地修改上传的Word后重新上传");
            if (!replacing.add(oldHash)) throw new ResumeException(409, "正在更新照片，请稍后操作");
        }
        try {
            return conversions.execute(() -> {
                String nextToken = storage.newToken();
                String nextHash = storage.hash(nextToken);
                Path work = storage.directory("work", nextHash);
                Files.createDirectory(work);
                boolean replaced = false;
                try {
                    byte[] normalized = photos.normalize(photo);
                    var snapshot = new ResumePreviewSnapshot();
                    snapshot.setTokenHash(nextHash);
                    snapshot.setSourceType("FORM");
                    snapshot.setForm(original.getForm());
                    snapshot.setAcademicYear(original.getAcademicYear());
                    snapshot.setDocumentName(original.getDocumentName());
                    snapshot.setTemplateVersion(original.getTemplateVersion());
                    snapshot.setExpiresAt(clock.instant().plusSeconds(properties.getPreviewMinutes() * 60L));
                    snapshot.setHasPhoto(true);
                    Files.write(work.resolve("photo.png"), normalized);
                    Path word = storage.file(work, snapshot.getDocumentName() + ".docx");
                    documents.generate(snapshot.getForm(), snapshot.getAcademicYear(), normalized, word);
                    conversions.convert(word);
                    if (Thread.currentThread().isInterrupted()) throw new ResumeException(503, "照片合成已取消，请重试");
                    synchronized (stateLock) {
                        var active = storage.load(token);
                        storage.publish(work, snapshot);
                        active.setSuperseded(true);
                        storage.writeSnapshot(storage.directory("previews", oldHash), active);
                        replaced = true;
                    }
                    return view(nextToken, snapshot);
                } finally {
                    storage.deleteTemporary(work);
                    if (!replaced) storage.deleteTemporary(storage.directory("previews", nextHash));
                }
            });
        } finally {
            synchronized (stateLock) { replacing.remove(oldHash); }
        }
    }

    private ResumePreviewVO view(String token, ResumePreviewSnapshot snapshot) {
        return new ResumePreviewVO(token, snapshot.getExpiresAt(), snapshot.getTemplateVersion(), snapshot.getSourceType(),
                "FORM".equals(snapshot.getSourceType()) ? snapshot.isHasPhoto() : null,
                snapshot.getDocumentName() + ".docx", snapshot.getDocumentName() + ".pdf");
    }

    private ResumeSubmission confirmed(String hash) {
        return submissions.selectOne(new QueryWrapper<ResumeSubmission>().eq("submission_key", hash));
    }

    @Override
    public com.bdis.bdis_website.vo.ResumeSettingsVO settings() {
        var rules = settings.selectById(1L);
        if (rules == null) throw new ResumeException(503, "报名设置尚未初始化，请联系管理员");
        return new com.bdis.bdis_website.vo.ResumeSettingsVO(rules.getSendMode(), rules.getRecipient(), mail.configured());
    }

    @Override
    public com.bdis.bdis_website.vo.ResumeSettingsVO updateSettings(com.bdis.bdis_website.dto.ResumeSettingsDTO form) {
        var rules = settings.selectById(1L);
        if (rules == null) throw new ResumeException(503, "报名设置尚未初始化，请联系管理员");
        rules.setSendMode(form.sendMode());
        rules.setRecipient(form.recipient().strip());
        rules.setUpdatedAt(LocalDateTime.now(clock));
        if (settings.updateById(rules) != 1) throw new ResumeException(500, "设置保存失败，请重试");
        return settings();
    }

    @Override
    public PageResult list(int page, int pageSize, String name, String studentNo, Integer mailStatus) {
        if (page < 1 || pageSize < 1 || pageSize > 50 || (mailStatus != null && (mailStatus < 0 || mailStatus > 3)))
            throw new ResumeException(400, "分页或邮件状态参数不正确");
        if ((name != null && name.length() > 80) || (studentNo != null && studentNo.length() > 80))
            throw new ResumeException(400, "搜索条件过长");
        var query = new QueryWrapper<ResumeSubmission>();
        if (name != null && !name.isBlank()) query.like("name", name.strip());
        if (studentNo != null && !studentNo.isBlank()) query.like("student_no", studentNo.strip());
        if (mailStatus != null) query.eq("mail_status", mailStatus);
        query.orderByDesc("created_at", "id");
        var result = submissions.selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize), query);
        return new PageResult(result.getRecords().stream().map(this::adminView).toList(), result.getTotal(), page, pageSize);
    }

    private com.bdis.bdis_website.vo.ResumeAdminVO adminView(ResumeSubmission record) {
        var result = new com.bdis.bdis_website.vo.ResumeAdminVO();
        org.springframework.beans.BeanUtils.copyProperties(record, result);
        result.setId(record.getId().toString());
        result.setFileAvailable(record.getDocumentName() != null && storage.savedFileExists(record.getWordKey()) && storage.savedFileExists(record.getPdfKey()));
        return result;
    }

    private ResumeSubmission requireRecord(long id) {
        var record = submissions.selectById(id);
        if (record == null) throw new ResumeException(404, "简历记录不存在");
        return record;
    }

    @Override
    public com.bdis.bdis_website.vo.ResumeAdminVO detail(long id) {
        return adminView(requireRecord(id));
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void recoverUncertainMail() {
        try {
            submissions.update(null, new UpdateWrapper<ResumeSubmission>().eq("mail_status", 1).set("mail_status", 3)
                    .set("mail_last_error", "上次发送结果待核实，请管理员确认后补发"));
        } catch (org.springframework.dao.DataAccessException e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("尚未完成简历表初始化，邮件结果恢复检查暂未执行");
        }
    }

    @Override
    public ResumeSubmitVO confirm(String token) {
        String hash = storage.hash(token);
        ResumeSubmission submission;
        String recipient;
        synchronized (stateLock) {
            submission = confirmed(hash);
            if (submission != null) return submitView(submission);
            if (replacing.contains(hash)) throw new ResumeException(409, "正在更新照片，请预览完成后再确认");
            var snapshot = storage.load(token);
            var rules = settings.selectById(1L);
            if (rules == null) throw new ResumeException(503, "报名设置尚未初始化，请联系管理员");
            String directoryKey = storage.hash(storage.newToken());
            Path destination = storage.directory("submissions", directoryKey);
            try {
                Path source = storage.directory("previews", hash);
                Path word = storage.file(source, snapshot.getDocumentName() + ".docx");
                Path pdf = storage.file(source, snapshot.getDocumentName() + ".pdf");
                if (!Files.isRegularFile(word)) throw new ResumeException(409, "预览文件不完整，请重新生成");
                conversions.validatePdf(pdf);
                Files.createDirectory(destination);
                Files.copy(word, storage.file(destination, snapshot.getDocumentName() + ".docx"));
                Files.copy(pdf, storage.file(destination, snapshot.getDocumentName() + ".pdf"));
                var record = new ResumeSubmission();
                org.springframework.beans.BeanUtils.copyProperties(snapshot.getForm(), record);
                record.setName(record.getName().strip());
                record.setStudentNo(record.getStudentNo().strip());
                record.setPhone(record.getPhone().strip());
                record.setAcademicYear(snapshot.getAcademicYear());
                record.setDocumentName(snapshot.getDocumentName());
                record.setTemplateVersion(snapshot.getTemplateVersion());
                record.setSubmissionSource(snapshot.getSourceType());
                record.setSubmissionKey(hash);
                record.setWordKey(directoryKey + "/" + snapshot.getDocumentName() + ".docx");
                record.setPdfKey(directoryKey + "/" + snapshot.getDocumentName() + ".pdf");
                record.setStatus(0);
                record.setMailStatus(0);
                record.setSubmitMailMode(rules.getSendMode());
                record.setCreatedAt(LocalDateTime.now(clock));
                transactions.executeWithoutResult(status -> {
                    if (submissions.insert(record) != 1) throw new ResumeException(500, "保存失败，请重试");
                });
                submission = record;
                recipient = rules.getRecipient();
            } catch (Exception e) {
                ResumeSubmission persisted;
                try {
                    persisted = confirmed(hash);
                } catch (org.springframework.dao.DataAccessException unavailable) {
                    throw new ResumeException(503, "保存结果暂待核实，请稍后使用同一预览重试，勿重新填写");
                }
                if (persisted == null) {
                    storage.deleteUncommittedSubmission(destination);
                    if (e instanceof ResumeException failure) throw failure;
                    throw new ResumeException(500, "报名保存失败，请稍后重试，原预览仍可继续使用");
                }
                submission = persisted;
                recipient = rules.getRecipient();
            }
        }
        if ("AUTO".equals(submission.getSubmitMailMode())) {
            try {
                attemptMail(submission, recipient);
            } catch (RuntimeException e) {
                submission.setMailStatus(3);
            }
        }
        try {
            return submitView(submissions.selectById(submission.getId()));
        } catch (org.springframework.dao.DataAccessException unavailable) {
            return submitView(submission);
        }
    }

    private void attemptMail(ResumeSubmission record, String recipient) {
        if (submissions.claimMail(record.getId(), recipient) != 1) throw new ResumeException(409, "该简历正在发送，请勿重复操作");
        int result = 2;
        String error = null;
        try {
            mail.send(record, recipient);
        } catch (Exception e) {
            result = 3;
            error = e instanceof ResumeException ? e.getMessage() : "邮件未发送成功，请检查发件配置后补发";
        }
        record.setMailStatus(result);
        try {
            submissions.update(null, new UpdateWrapper<ResumeSubmission>().eq("id", record.getId()).eq("mail_status", 1)
                    .set("mail_status", result).set("mail_last_error", error)
                    .set("mail_sent_at", result == 2 ? LocalDateTime.now(clock) : null));
        } catch (org.springframework.dao.DataAccessException e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).error("邮件结果写入失败，请管理员核实后补发");
        }
    }

    private ResumeSubmitVO submitView(ResumeSubmission record) {
        int status = record.getMailStatus() == null ? 0 : record.getMailStatus();
        String message = switch (status) {
            case 2 -> "报名已保存，邮件服务器已接受发送";
            case 1 -> "报名已保存，邮件处理中或结果待核实，请勿重复提交";
            case 3 -> "报名已保存，邮件暂未成功，管理员可稍后补发";
            default -> "报名已保存，等待管理员发送";
        };
        return new ResumeSubmitVO(record.getId().toString(), record.getCreatedAt().atZone(clock.getZone()).toOffsetDateTime(), status, message);
    }

    @Override
    public ResumePreviewVO upload(com.bdis.bdis_website.dto.ResumeUploadDTO index,
                                  org.springframework.web.multipart.MultipartFile upload, String sourceToken) {
        if (upload.getSize() > 10L * 1024 * 1024) throw new ResumeException(413, "Word文档不能超过10MB");
        String year = sourceToken == null || sourceToken.isBlank() ? documents.currentAcademicYear() : storage.load(sourceToken).getAcademicYear();
        return conversions.execute(() -> {
            byte[] bytes = documents.validateUpload(upload);
            String token = storage.newToken();
            String hash = storage.hash(token);
            Path work = storage.directory("work", hash);
            Files.createDirectory(work);
            try {
                var form = new ResumeFormDTO();
                form.setName(index.getName().strip());
                form.setStudentNo(index.getStudentNo().strip());
                form.setPhone(index.getPhone().strip());
                var snapshot = new ResumePreviewSnapshot();
                snapshot.setForm(form);
                snapshot.setTokenHash(hash);
                snapshot.setSourceType("UPLOAD");
                snapshot.setAcademicYear(year);
                snapshot.setDocumentName(documents.documentName(form.getName(), form.getStudentNo(), year));
                snapshot.setTemplateVersion("uploaded");
                snapshot.setExpiresAt(clock.instant().plusSeconds(properties.getPreviewMinutes() * 60L));
                Path word = storage.file(work, snapshot.getDocumentName() + ".docx");
                Files.write(word, bytes);
                conversions.convert(word);
                storage.publish(work, snapshot);
                return view(token, snapshot);
            } finally {
                storage.deleteTemporary(work);
            }
        });
    }
}
