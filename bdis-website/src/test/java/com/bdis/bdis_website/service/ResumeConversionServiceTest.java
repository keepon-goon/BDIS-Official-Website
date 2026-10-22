package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.exception.ResumeException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

class ResumeConversionServiceTest {
    @TempDir Path temporary;

    private ThreadPoolExecutor executor() {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2));
    }

    @Test
    void fourthTaskIsRejectedWithoutUnboundedQueue() throws Exception {
        var pool = executor();
        var release = new CountDownLatch(1);
        var started = new CountDownLatch(1);
        try {
            pool.submit(() -> { started.countDown(); release.await(); return null; });
            assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
            pool.submit(() -> { release.await(); return null; });
            pool.submit(() -> { release.await(); return null; });
            var service = new ResumeConversionService(new ResumeProperties(), pool);
            assertThatThrownBy(() -> service.execute(() -> "extra"))
                    .isInstanceOfSatisfying(ResumeException.class, e -> assertThat(e.getStatus()).isEqualTo(429));
        } finally { release.countDown(); pool.shutdownNow(); pool.awaitTermination(2, TimeUnit.SECONDS); }
    }

    @Test
    void timeoutInterruptsTaskAndFreesWorker() throws Exception {
        var pool = executor();
        var properties = new ResumeProperties();
        properties.setRequestTimeoutSeconds(1);
        var interrupted = new AtomicBoolean();
        try {
            var service = new ResumeConversionService(properties, pool);
            assertThatThrownBy(() -> service.execute(() -> {
                try { Thread.sleep(10000); } catch (InterruptedException e) { interrupted.set(true); throw e; }
                return null;
            })).isInstanceOfSatisfying(ResumeException.class, e -> assertThat(e.getStatus()).isEqualTo(503));
            assertThat(pool.submit(() -> "ready").get(2, TimeUnit.SECONDS)).isEqualTo("ready");
            assertThat(interrupted).isTrue();
        } finally { pool.shutdownNow(); pool.awaitTermination(2, TimeUnit.SECONDS); }
    }

    @Test
    void rejectsThreePageAndEmptyPdf() throws Exception {
        var pool = executor();
        try {
            var service = new ResumeConversionService(new ResumeProperties(), pool);
            Path pdf = temporary.resolve("three.pdf");
            try (var document = new PDDocument()) {
                for (int i = 0; i < 3; i++) document.addPage(new PDPage());
                document.save(pdf.toFile());
            }
            assertThatThrownBy(() -> service.validatePdf(pdf)).isInstanceOfSatisfying(ResumeException.class,
                    e -> assertThat(e.getStatus()).isEqualTo(422));
            Files.write(pdf, new byte[0]);
            assertThatThrownBy(() -> service.validatePdf(pdf)).isInstanceOf(ResumeException.class);
        } finally { pool.shutdownNow(); }
    }

    @Test
    void missingConverterFailsWithoutPublishingOutput() throws Exception {
        var pool = executor();
        try {
            var properties = new ResumeProperties();
            properties.setLibreofficePath(temporary.resolve("missing-soffice").toString());
            var service = new ResumeConversionService(properties, pool);
            Path word = temporary.resolve("resume.docx");
            Files.write(word, new byte[]{80, 75});
            assertThatThrownBy(() -> service.convert(word)).isInstanceOfSatisfying(ResumeException.class,
                    e -> assertThat(e.getStatus()).isEqualTo(503));
            assertThat(Files.exists(temporary.resolve("resume.pdf"))).isFalse();
        } finally { pool.shutdownNow(); }
    }
}
