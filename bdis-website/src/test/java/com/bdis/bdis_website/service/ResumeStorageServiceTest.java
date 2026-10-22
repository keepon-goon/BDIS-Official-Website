package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.model.ResumePreviewSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.*;

import static org.assertj.core.api.Assertions.*;

class ResumeStorageServiceTest {
    @TempDir Path temporary;

    @Test
    void cleansExpiredPreviewAndAbandonedWorkButNeverFormalFiles() throws Exception {
        var properties = new ResumeProperties();
        properties.setStorageRoot(temporary.resolve("private").toString());
        Instant now = Instant.parse("2026-10-22T00:00:00Z");
        var storage = new ResumeStorageService(properties, new ObjectMapper().findAndRegisterModules(),
                Clock.fixed(now, ZoneOffset.UTC), temporary.resolve("public").toString());
        String hash = storage.hash(storage.newToken());
        var snapshot = new ResumePreviewSnapshot();
        snapshot.setTokenHash(hash);
        snapshot.setExpiresAt(now.minusSeconds(601));
        var preview = storage.directory("previews", hash);
        Files.createDirectory(preview);
        storage.writeSnapshot(preview, snapshot);
        var work = storage.directory("work", hash);
        Files.createDirectory(work);
        Files.setLastModifiedTime(work, FileTime.from(now.minusSeconds(7201)));
        var formal = storage.directory("submissions", hash);
        Files.createDirectory(formal);
        Files.writeString(formal.resolve("keep.txt"), "保留正式文件");
        storage.cleanup();
        assertThat(Files.exists(preview)).isFalse();
        assertThat(Files.exists(work)).isFalse();
        assertThat(Files.readString(formal.resolve("keep.txt"))).isEqualTo("保留正式文件");
    }

    @Test
    void refusesPublicStorageRoot() {
        var properties = new ResumeProperties();
        properties.setStorageRoot(temporary.resolve("public/private").toString());
        assertThatThrownBy(() -> new ResumeStorageService(properties, new ObjectMapper(), Clock.systemUTC(),
                temporary.resolve("public").toString())).isInstanceOf(IllegalStateException.class);
    }
}
