package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.exception.ResumeException;
import org.apache.pdfbox.Loader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@Service
public class ResumeConversionService {
    private final ResumeProperties properties;
    private final ThreadPoolExecutor executor;

    public ResumeConversionService(ResumeProperties properties, ThreadPoolExecutor resumeExecutor) {
        this.properties = properties;
        this.executor = resumeExecutor;
    }

    public <T> T execute(Callable<T> task) {
        Future<T> future;
        try {
            future = executor.submit(task);
        } catch (RejectedExecutionException e) {
            throw new ResumeException(429, "当前生成任务较多，请稍后重试");
        }
        try {
            return future.get(properties.getRequestTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            executor.purge();
            throw new ResumeException(503, "预览生成超时，请稍后重试或下载模板手工填写");
        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new ResumeException(503, "预览生成已中断，请重试");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ResumeException failure) throw failure;
            throw new ResumeException(503, "预览生成失败，请重试或下载模板手工填写后上传");
        }
    }

    public Path convert(Path word) {
        Path directory = word.getParent();
        Path profile = directory.resolve("profile");
        String container = "bdis-resume-" + UUID.randomUUID();
        boolean docker = "docker".equals(properties.getConverter());
        List<String> command = new ArrayList<>();
        try {
            Files.createDirectories(profile.resolve("user"));
            Files.writeString(profile.resolve("user/registrymodifications.xcu"),
                    "<?xml version=\"1.0\"?><oor:items xmlns:oor=\"http://openoffice.org/2001/registry\">"
                    + "<item oor:path=\"/org.openoffice.Office.Common/Security/Scripting\"><prop oor:name=\"MacroSecurityLevel\" oor:op=\"fuse\"><value>3</value></prop></item></oor:items>");
            if (docker) {
                command.addAll(List.of(properties.getDockerPath(), "run", "--rm", "--name", container,
                        "--network", "none", "--cpus", "1", "--memory", "768m", "--pids-limit", "256",
                        "--mount", "type=bind,source=" + directory.toAbsolutePath() + ",target=/data",
                        properties.getDockerImage(), "soffice", "-env:UserInstallation=file:///data/profile"));
            } else {
                command.addAll(List.of(properties.getLibreofficePath(), "-env:UserInstallation=" + profile.toUri()));
            }
            command.addAll(List.of("--headless", "--nologo", "--nodefault", "--norestore", "--convert-to", "pdf:writer_pdf_Export",
                    "--outdir", docker ? "/data" : directory.toString(), docker ? "/data/" + word.getFileName() : word.toString()));
            Process process = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(directory.resolve("conversion.log").toFile()).start();
            try {
                if (!process.waitFor(properties.getConversionTimeoutSeconds(), TimeUnit.SECONDS)) {
                    throw new ResumeException(503, "Word转PDF超时，请重试或使用手工上传");
                }
                if (process.exitValue() != 0) throw new ResumeException(503, "Word转PDF失败，请检查文件后重试");
            } finally {
                if (process.isAlive()) {
                    process.descendants().forEach(handle -> handle.destroyForcibly());
                    process.destroyForcibly();
                    if (docker) {
                        Process cleanup = new ProcessBuilder(properties.getDockerPath(), "rm", "-f", container)
                                .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
                        if (!cleanup.waitFor(5, TimeUnit.SECONDS)) cleanup.destroyForcibly();
                    }
                }
            }
            String filename = word.getFileName().toString();
            Path pdf = directory.resolve(filename.substring(0, filename.length() - 5) + ".pdf");
            validatePdf(pdf);
            return pdf;
        } catch (IOException e) {
            throw new ResumeException(503, "转换工具不可用或文件无法读取，请稍后重试");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResumeException(503, "转换已中断，请重试");
        }
    }

    public void validatePdf(Path pdf) {
        try {
            if (!Files.isRegularFile(pdf) || Files.size(pdf) == 0) throw new IOException();
            try (var document = Loader.loadPDF(pdf.toFile())) {
                if (document.getNumberOfPages() < 1 || document.getNumberOfPages() > 2)
                    throw new ResumeException(422, "简历必须为一到两页，请精简内容或下载模板调整后上传Word");
            }
        } catch (IOException e) {
            throw new ResumeException(503, "未生成可读取的PDF，请重试或下载模板手工填写");
        }
    }
}
