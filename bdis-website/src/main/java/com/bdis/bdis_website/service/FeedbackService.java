package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdis.bdis_website.entity.FeedbackImage;
import com.bdis.bdis_website.entity.FeedbackSubmission;
import com.bdis.bdis_website.exception.FeedbackException;
import com.bdis.bdis_website.mapper.FeedbackImageMapper;
import com.bdis.bdis_website.mapper.FeedbackSubmissionMapper;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifIFD0Directory;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    public static final int MAX_IMAGES = 6;
    public static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;
    public static final Set<String> CATEGORIES = Set.of(
            "join", "website", "equipment", "operation", "other",
            "suggestion", "activity"
    );
    public static final Set<String> STATUSES = Set.of("UNREAD", "PROCESSING", "RESOLVED");
    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    private final FeedbackSubmissionMapper submissions;
    private final FeedbackImageMapper images;
    private final TransactionTemplate transactions;
    private final Clock clock;
    private final FeedbackMailService mailService;
    private final Path root;
    private final Semaphore imageSlots = new Semaphore(2);

    public FeedbackService(FeedbackSubmissionMapper submissions, FeedbackImageMapper images,
                           PlatformTransactionManager transactionManager, Clock clock,
                           FeedbackMailService mailService,
                           @Value("${feedback.storage-root:./private-feedback}") String storageRoot) {
        this.submissions = submissions;
        this.images = images;
        this.transactions = new TransactionTemplate(transactionManager);
        this.clock = clock;
        this.mailService = mailService;
        this.root = Path.of(storageRoot).toAbsolutePath().normalize();
    }

    public void submit(String category, String content, List<MultipartFile> files,
                       boolean replyRequested, boolean publicReply, String displayName, String replyEmail) {
        String text = content == null ? "" : content.replace("\r\n", "\n").replace('\r', '\n').strip();
        String selectedCategory = category == null || category.isBlank() ? "other" : category;
        List<MultipartFile> uploads = files == null ? List.of() : files;
        if (!CATEGORIES.contains(selectedCategory)) throw new FeedbackException(400, "请选择有效的意见分类");
        if (text.length() > 3000) throw new FeedbackException(400, "意见内容不能超过3000字");
        if (uploads.size() > MAX_IMAGES) throw new FeedbackException(400, "每次最多上传6张图片");
        if (text.isBlank() && uploads.isEmpty()) throw new FeedbackException(400, "请填写意见或添加至少一张图片");
        String name = "匿名";
        String email = "";
        if (replyRequested && publicReply) {
            name = displayName == null || displayName.isBlank() ? "匿名" : displayName.strip();
            if (name.length() > 40) throw new FeedbackException(400, "称呼不能超过40字");
        } else if (replyRequested) {
            email = replyEmail == null ? "" : replyEmail.strip();
            if (email.length() > 254 || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
                throw new FeedbackException(400, "请填写有效的回信邮箱");
            try {
                InternetAddress address = new InternetAddress(email, true);
                address.validate();
                if (address.getPersonal() != null || address.isGroup()) throw new AddressException();
            } catch (AddressException e) {
                throw new FeedbackException(400, "请填写有效的回信邮箱");
            }
        }
        for (MultipartFile file : uploads) {
            if (file == null || file.isEmpty()) throw new FeedbackException(400, "图片为空，请重新选择");
            if (file.getSize() > MAX_IMAGE_BYTES) throw new FeedbackException(413, "每张图片不能超过5MB");
        }
        boolean acquired = !uploads.isEmpty();
        if (acquired && !imageSlots.tryAcquire()) throw new FeedbackException(429, "当前图片投递较多，请稍后重试");
        List<Path> written = new ArrayList<>();
        try {
            FeedbackSubmission submission = new FeedbackSubmission();
            submission.setId(UUID.randomUUID().toString());
            submission.setCategory(selectedCategory);
            submission.setContent(text);
            submission.setStatus("UNREAD");
            submission.setReplyRequested(replyRequested);
            submission.setPublicReply(replyRequested && publicReply);
            submission.setDisplayName(name);
            submission.setReplyEmail(email);
            submission.setPublished(false);
            submission.setCreatedAt(LocalDateTime.now(clock));
            submission.setUpdatedAt(submission.getCreatedAt());
            List<FeedbackImage> attachments = new ArrayList<>();
            for (MultipartFile file : uploads) {
                byte[] normalized = normalize(file);
                FeedbackImage image = new FeedbackImage();
                image.setId(UUID.randomUUID().toString());
                image.setSubmissionId(submission.getId());
                image.setStorageKey(image.getId() + ".png");
                image.setByteSize((long) normalized.length);
                Files.createDirectories(root);
                Path destination = root.resolve(image.getStorageKey());
                written.add(destination);
                Files.write(destination, normalized);
                attachments.add(image);
            }
            transactions.executeWithoutResult(transaction -> {
                submissions.insert(submission);
                attachments.forEach(images::insert);
            });
            mailService.sendNotification(submission, attachments, root);
        } catch (IOException e) {
            cleanup(written);
            throw new FeedbackException(500, "图片保存失败，请稍后重试");
        } catch (RuntimeException e) {
            cleanup(written);
            throw e;
        } finally {
            if (acquired) imageSlots.release();
        }
    }

    public Map<String, Object> list(int page, int pageSize, String status, String category) {
        if (page < 1 || page > 100000 || pageSize < 1 || pageSize > 50)
            throw new FeedbackException(400, "分页参数无效");
        LambdaQueryWrapper<FeedbackSubmission> query = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            if (!STATUSES.contains(status)) throw new FeedbackException(400, "处理状态无效");
            query.eq(FeedbackSubmission::getStatus, status);
        }
        if (category != null && !category.isBlank()) {
            if (!CATEGORIES.contains(category)) throw new FeedbackException(400, "意见分类无效");
            query.eq(FeedbackSubmission::getCategory, category);
        }
        query.orderByDesc(FeedbackSubmission::getCreatedAt).orderByDesc(FeedbackSubmission::getId);
        Page<FeedbackSubmission> result = submissions.selectPage(new Page<>(page, pageSize), query);
        List<String> ids = result.getRecords().stream().map(FeedbackSubmission::getId).toList();
        Map<String, List<FeedbackImage>> attachments = ids.isEmpty() ? Map.of() : images.selectList(
                new LambdaQueryWrapper<FeedbackImage>().in(FeedbackImage::getSubmissionId, ids))
                .stream().collect(Collectors.groupingBy(FeedbackImage::getSubmissionId));
        List<FeedbackView> records = result.getRecords().stream()
                .map(item -> view(item, attachments.getOrDefault(item.getId(), List.of()))).toList();
        return Map.of("list", records, "total", result.getTotal(), "page", page, "pageSize", pageSize,
                "counts", Map.of("total", submissions.selectCount(null),
                        "UNREAD", count("UNREAD"), "PROCESSING", count("PROCESSING"), "RESOLVED", count("RESOLVED")));
    }

    private long count(String status) {
        return submissions.selectCount(new LambdaQueryWrapper<FeedbackSubmission>().eq(FeedbackSubmission::getStatus, status));
    }

    public FeedbackView detail(String id) {
        FeedbackSubmission item = submissions.selectById(id);
        if (item == null) throw new FeedbackException(404, "这条意见不存在");
        return view(item, images.selectList(new LambdaQueryWrapper<FeedbackImage>().eq(FeedbackImage::getSubmissionId, id)
                .orderByAsc(FeedbackImage::getId)));
    }

    public void updateStatus(String id, String status) {
        if (!STATUSES.contains(status == null ? "" : status)) throw new FeedbackException(400, "处理状态无效");
        int updated = submissions.update(null, new LambdaUpdateWrapper<FeedbackSubmission>()
                .eq(FeedbackSubmission::getId, id).set(FeedbackSubmission::getStatus, status)
                .set(FeedbackSubmission::getUpdatedAt, LocalDateTime.now(clock)));
        if (updated == 0) throw new FeedbackException(404, "这条意见不存在");
    }

    public void saveReply(String id, String replyContent, boolean published) {
        FeedbackSubmission item = submissions.selectById(id);
        if (item == null) throw new FeedbackException(404, "这条意见不存在");
        if (!Boolean.TRUE.equals(item.getReplyRequested())) throw new FeedbackException(400, "投稿人未选择接收回复");
        if (published && !Boolean.TRUE.equals(item.getPublicReply())) throw new FeedbackException(400, "投稿人未同意公开，不能公开这条问答");
        String answer = replyContent == null ? "" : replyContent.replace("\r\n", "\n").replace('\r', '\n').strip();
        if (answer.isBlank() || answer.length() > 3000) throw new FeedbackException(400, "请填写1到3000字的回复");
        LocalDateTime now = LocalDateTime.now(clock);
        boolean changed = !answer.equals(item.getReplyContent());
        LambdaUpdateWrapper<FeedbackSubmission> update = new LambdaUpdateWrapper<FeedbackSubmission>()
                .eq(FeedbackSubmission::getId, id).set(FeedbackSubmission::getReplyContent, answer)
                .set(FeedbackSubmission::getPublished, published).set(FeedbackSubmission::getStatus, "RESOLVED")
                .set(FeedbackSubmission::getRepliedAt, changed ? now : item.getRepliedAt())
                .set(FeedbackSubmission::getUpdatedAt, now);
        if (changed) update.set(FeedbackSubmission::getReplyEmailedAt, null);
        submissions.update(null, update);
    }

    public void sendReplyEmail(String id) {
        FeedbackSubmission item = submissions.selectById(id);
        if (item == null) throw new FeedbackException(404, "这条意见不存在");
        if (!Boolean.TRUE.equals(item.getReplyRequested()) || Boolean.TRUE.equals(item.getPublicReply()) || item.getReplyEmail().isBlank())
            throw new FeedbackException(400, "这条意见没有选择邮箱回信");
        if (item.getReplyContent() == null || item.getReplyContent().isBlank()) throw new FeedbackException(400, "请先保存回复");
        mailService.sendReply(item);
        LocalDateTime now = LocalDateTime.now(clock);
        submissions.update(null, new LambdaUpdateWrapper<FeedbackSubmission>().eq(FeedbackSubmission::getId, id)
                .set(FeedbackSubmission::getReplyEmailedAt, now).set(FeedbackSubmission::getUpdatedAt, now));
    }

    public Map<String, Object> publicQa(int page, int pageSize) {
        if (page < 1 || page > 100000 || pageSize < 1 || pageSize > 50) throw new FeedbackException(400, "分页参数无效");
        Page<FeedbackSubmission> result = submissions.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<FeedbackSubmission>().eq(FeedbackSubmission::getReplyRequested, true)
                        .eq(FeedbackSubmission::getPublicReply, true).eq(FeedbackSubmission::getPublished, true)
                        .isNotNull(FeedbackSubmission::getRepliedAt).isNotNull(FeedbackSubmission::getReplyContent)
                        .ne(FeedbackSubmission::getReplyContent, "")
                        .orderByDesc(FeedbackSubmission::getRepliedAt).orderByDesc(FeedbackSubmission::getId));
        List<PublicQaView> records = result.getRecords().stream().map(item -> new PublicQaView(item.getId(),
                item.getContent(), item.getReplyContent(), item.getDisplayName(), item.getRepliedAt())).toList();
        return Map.of("list", records, "total", result.getTotal(), "page", page, "pageSize", pageSize);
    }

    public byte[] image(String submissionId, String imageId) {
        FeedbackImage image = images.selectOne(new LambdaQueryWrapper<FeedbackImage>()
                .eq(FeedbackImage::getId, imageId).eq(FeedbackImage::getSubmissionId, submissionId));
        if (image == null) throw new FeedbackException(404, "图片不存在");
        Path source = root.resolve(image.getStorageKey()).normalize();
        if (!source.startsWith(root)) throw new FeedbackException(404, "图片不存在");
        try {
            return Files.readAllBytes(source);
        } catch (IOException e) {
            throw new FeedbackException(404, "图片暂时无法读取");
        }
    }

    private FeedbackView view(FeedbackSubmission item, List<FeedbackImage> attachments) {
        return new FeedbackView(item.getId(), item.getCategory(), item.getContent(), item.getStatus(), item.getCreatedAt(),
                item.getUpdatedAt(), item.getReplyRequested(), item.getPublicReply(), item.getDisplayName(), item.getReplyEmail(),
                item.getReplyContent(), item.getPublished(), item.getRepliedAt(), item.getReplyEmailedAt(),
                attachments.stream().map(image -> new ImageView(image.getId(), image.getByteSize(),
                        "/api/admin/feedback/" + item.getId() + "/images/" + image.getId())).toList());
    }

    public record ImageView(String id, long size, String url) {}
    public record FeedbackView(String id, String category, String content, String status,
                               LocalDateTime createdAt, LocalDateTime updatedAt, Boolean replyRequested, Boolean publicReply,
                               String displayName, String replyEmail, String replyContent, Boolean published,
                               LocalDateTime repliedAt, LocalDateTime replyEmailedAt, List<ImageView> images) {}
    public record PublicQaView(String id, String question, String answer, String displayName, LocalDateTime repliedAt) {}

    // Decode and re-encode rather than retaining uploaded filenames or EXIF metadata.
    private byte[] normalize(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
                var readers = ImageIO.getImageReaders(input);
                if (!readers.hasNext()) throw new FeedbackException(400, "仅支持可正常读取的JPG或PNG图片");
                var reader = readers.next();
                try {
                    String format = reader.getFormatName();
                    if (!format.equalsIgnoreCase("JPEG") && !format.equalsIgnoreCase("PNG"))
                        throw new FeedbackException(400, "图片仅支持JPG或PNG格式");
                    reader.setInput(input, false, true);
                    int width = reader.getWidth(0), height = reader.getHeight(0);
                    if (width <= 0 || height <= 0 || (long) width * height > 40_000_000)
                        throw new FeedbackException(400, "图片尺寸过大，请缩小后重试");
                    var parameters = reader.getDefaultReadParam();
                    int sample = Math.max(1, (Math.max(width, height) + 2399) / 2400);
                    parameters.setSourceSubsampling(sample, sample, 0, 0);
                    BufferedImage decoded = reader.read(0, parameters);
                    width = decoded.getWidth();
                    height = decoded.getHeight();
                    int orientation = orientation(bytes);
                    AffineTransform transform = switch (orientation) {
                        case 2 -> new AffineTransform(-1, 0, 0, 1, width, 0);
                        case 3 -> new AffineTransform(-1, 0, 0, -1, width, height);
                        case 4 -> new AffineTransform(1, 0, 0, -1, 0, height);
                        case 5 -> new AffineTransform(0, 1, 1, 0, 0, 0);
                        case 6 -> new AffineTransform(0, 1, -1, 0, height, 0);
                        case 7 -> new AffineTransform(0, -1, -1, 0, height, width);
                        case 8 -> new AffineTransform(0, -1, 1, 0, 0, width);
                        default -> new AffineTransform();
                    };
                    BufferedImage normalized = new BufferedImage(orientation >= 5 ? height : width,
                            orientation >= 5 ? width : height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = normalized.createGraphics();
                    try { graphics.drawImage(decoded, transform, null); } finally { graphics.dispose(); }
                    try (var output = new ByteArrayOutputStream()) {
                        ImageIO.write(normalized, "png", output);
                        return output.toByteArray();
                    }
                } finally { reader.dispose(); }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new FeedbackException(400, "图片无法读取，请更换后重试");
        }
    }

    private int orientation(byte[] bytes) {
        try {
            var directory = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes))
                    .getFirstDirectoryOfType(ExifIFD0Directory.class);
            Integer value = directory == null ? null : directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            return value != null && value >= 1 && value <= 8 ? value : 1;
        } catch (Exception ignored) { return 1; }
    }

    private void cleanup(List<Path> files) {
        for (Path file : files) {
            try { Files.deleteIfExists(file); }
            catch (IOException e) { log.warn("Failed to remove an uncommitted feedback attachment", e); }
        }
    }
}
