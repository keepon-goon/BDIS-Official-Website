package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.exception.ResumeException;
import com.bdis.bdis_website.model.ResumePreviewSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Comparator;
import java.util.HexFormat;

@Service
public class ResumeStorageService {
    private final Path root;
    private final ObjectMapper json;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public ResumeStorageService(ResumeProperties properties, ObjectMapper json, Clock resumeClock,
                                @Value("${upload.path:./uploads}") String uploadPath) throws IOException {
        Path configured = Path.of(properties.getStorageRoot()).toAbsolutePath().normalize();
        Files.createDirectories(configured);
        root = configured.toRealPath();
        if (root.startsWith(Path.of(uploadPath).toAbsolutePath().normalize())
                || root.startsWith(Path.of("../bdis-web/public").toAbsolutePath().normalize())
                || root.startsWith(Path.of("src/main/resources/static").toAbsolutePath().normalize())) {
            throw new IllegalStateException("简历私有目录不能位于公开静态目录内");
        }
        for (String part : new String[]{"previews", "submissions", "work"}) {
            Path child = root.resolve(part);
            if (Files.isSymbolicLink(child)) throw new IllegalStateException("简历目录不能使用符号链接");
            Files.createDirectories(child);
        }
        this.json = json;
        this.clock = resumeClock;
    }

    public String newToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public String hash(String token) {
        if (token == null || !token.matches("[a-f0-9]{64}")) throw new ResumeException(404, "预览不存在或凭证无效");
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.US_ASCII)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public Path directory(String area, String key) {
        if (!key.matches("[a-f0-9]{64}")) throw new ResumeException(404, "文件不存在");
        Path path = root.resolve(area).resolve(key).normalize();
        if (!path.startsWith(root) || Files.isSymbolicLink(path)) throw new ResumeException(404, "文件不存在");
        return path;
    }

    public Path file(Path directory, String name) {
        Path path = directory.resolve(name).normalize();
        if (!path.getParent().equals(directory) || Files.isSymbolicLink(path)) throw new ResumeException(404, "文件不存在");
        return path;
    }

    public void writeSnapshot(Path directory, ResumePreviewSnapshot snapshot) throws IOException {
        Path temporary = directory.resolve("snapshot.tmp");
        json.writeValue(temporary.toFile(), snapshot);
        try {
            Files.move(temporary, directory.resolve("snapshot.json"), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporary, directory.resolve("snapshot.json"), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public ResumePreviewSnapshot load(String token) {
        Path directory = directory("previews", hash(token));
        try {
            ResumePreviewSnapshot snapshot = json.readValue(file(directory, "snapshot.json").toFile(), ResumePreviewSnapshot.class);
            if (!snapshot.getTokenHash().equals(hash(token))) throw new ResumeException(404, "预览不存在");
            if (snapshot.isSuperseded() || !snapshot.getExpiresAt().isAfter(clock.instant()))
                throw new ResumeException(410, "预览已过期或已被新版替换，请重新生成或使用当前版本");
            return snapshot;
        } catch (NoSuchFileException | java.io.FileNotFoundException e) {
            throw new ResumeException(404, "预览不存在，请重新生成");
        } catch (IOException e) {
            throw new ResumeException(503, "预览暂不可读，请重新生成");
        }
    }

    public void publish(Path work, ResumePreviewSnapshot snapshot) throws IOException {
        writeSnapshot(work, snapshot);
        Files.move(work, directory("previews", snapshot.getTokenHash()));
    }

    public byte[] savedFile(String key) {
        if (key == null || !key.matches("[a-f0-9]{64}/[^/\\\\]+"))
            throw new ResumeException(409, "历史记录或正式文件未就绪，无法读取新版简历");
        int separator = key.indexOf('/');
        Path path = file(directory("submissions", key.substring(0, separator)), key.substring(separator + 1));
        try {
            if (!Files.isRegularFile(path)) throw new ResumeException(409, "正式文件缺失，请联系管理员核实");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ResumeException(503, "正式文件暂不可读，请稍后重试");
        }
    }

    public boolean savedFileExists(String key) {
        if (key == null || !key.matches("[a-f0-9]{64}/[^/\\\\]+")) return false;
        try {
            int separator = key.indexOf('/');
            return Files.isRegularFile(file(directory("submissions", key.substring(0, separator)), key.substring(separator + 1)));
        } catch (ResumeException e) {
            return false;
        }
    }

    public void deleteTemporary(Path directory) {
        if (directory == null || !(directory.startsWith(root.resolve("work"))
                || directory.startsWith(root.resolve("previews")))) return;
        deleteOwned(directory);
    }

    public void deleteUncommittedSubmission(Path directory) {
        if (directory != null && directory.getParent().equals(root.resolve("submissions"))) deleteOwned(directory);
    }

    private void deleteOwned(Path directory) {
        try {
            if (!Files.exists(directory)) return;
            try (var stream = Files.walk(directory)) {
                for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("简历临时目录清理失败，稍后重试");
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void cleanup() {
        try (var stream = Files.list(root.resolve("previews"))) {
            for (Path directory : stream.toList()) {
                if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) continue;
                try {
                    ResumePreviewSnapshot snapshot = json.readValue(directory.resolve("snapshot.json").toFile(), ResumePreviewSnapshot.class);
                    if (snapshot.getExpiresAt().isBefore(clock.instant().minusSeconds(600))) deleteTemporary(directory);
                } catch (IOException ignored) {
                    if (Files.getLastModifiedTime(directory).toInstant().isBefore(clock.instant().minusSeconds(7200))) deleteTemporary(directory);
                }
            }
        } catch (IOException e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("简历过期检查暂不可用");
        }
    }
}
