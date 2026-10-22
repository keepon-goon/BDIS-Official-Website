package com.bdis.bdis_website.service;

import com.bdis.bdis_website.exception.ResumeException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ResumePhotoService {
    public static final long MAX_BYTES = 10L * 1024 * 1024;

    public byte[] normalize(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new ResumeException(400, "请选择照片");
        if (file.getSize() > MAX_BYTES) throw new ResumeException(413, "单张照片不能超过10MB");
        try {
            byte[] bytes = file.getBytes();
            try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
                var readers = ImageIO.getImageReaders(input);
                if (!readers.hasNext()) throw new ResumeException(400, "照片不是可读取的JPG或PNG图片");
                var reader = readers.next();
                try {
                    String format = reader.getFormatName();
                    if (!format.equalsIgnoreCase("JPEG") && !format.equalsIgnoreCase("PNG"))
                        throw new ResumeException(400, "照片仅支持JPG/JPEG或PNG");
                    reader.setInput(input, false, true);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    if (width <= 0 || height <= 0) throw new ResumeException(400, "照片尺寸无效，请更换图片");
                    var parameters = reader.getDefaultReadParam();
                    int sample = (int) Math.max(1L, ((long) Math.max(width, height) + 1799) / 1800);
                    parameters.setSourceSubsampling(sample, sample, 0, 0);
                    BufferedImage image = reader.read(0, parameters);
                    width = image.getWidth();
                    height = image.getHeight();
                    int orientation = orientation(bytes);
                    int displayWidth = orientation >= 5 ? height : width;
                    int displayHeight = orientation >= 5 ? width : height;
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
                    BufferedImage normalized = new BufferedImage(600, 900, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = normalized.createGraphics();
                    try {
                        graphics.setColor(Color.WHITE);
                        graphics.fillRect(0, 0, 600, 900);
                        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        double scale = Math.min(600.0 / displayWidth, 900.0 / displayHeight);
                        graphics.translate((600 - displayWidth * scale) / 2, (900 - displayHeight * scale) / 2);
                        graphics.scale(scale, scale);
                        graphics.drawImage(image, transform, null);
                    } finally {
                        graphics.dispose();
                    }
                    try (var output = new ByteArrayOutputStream()) {
                        ImageIO.write(normalized, "png", output);
                        return output.toByteArray();
                    }
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new ResumeException(400, "照片文件损坏或无法读取，请更换图片");
        }
    }

    private int orientation(byte[] bytes) {
        try {
            var directory = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes))
                    .getFirstDirectoryOfType(ExifIFD0Directory.class);
            Integer value = directory == null ? null : directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            return value != null && value >= 1 && value <= 8 ? value : 1;
        } catch (Exception e) {
            return 1;
        }
    }
}
