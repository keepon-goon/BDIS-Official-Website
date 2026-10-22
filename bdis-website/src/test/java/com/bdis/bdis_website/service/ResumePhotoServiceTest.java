package com.bdis.bdis_website.service;

import com.bdis.bdis_website.exception.ResumeException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.*;

class ResumePhotoServiceTest {
    private final ResumePhotoService service = new ResumePhotoService();

    private byte[] picture(int width, int height, String format) throws IOException {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var output = new ByteArrayOutputStream();
        ImageIO.write(image, format, output);
        return output.toByteArray();
    }

    @Test
    void acceptsPortraitRatiosAndNormalizesWithoutMetadata() throws Exception {
        for (int height : new int[]{600, 533}) {
            byte[] output = service.normalize(new MockMultipartFile("photo", "照片.png", "image/png", picture(400, height, "png")));
            var image = ImageIO.read(new ByteArrayInputStream(output));
            assertThat(image.getWidth()).isEqualTo(600);
            assertThat(image.getHeight()).isEqualTo(900);
        }
    }

    @Test
    void rejectsSquareAndInvalidImage() throws Exception {
        assertThatThrownBy(() -> service.normalize(new MockMultipartFile("photo", picture(400, 400, "png"))))
                .isInstanceOf(ResumeException.class).hasMessageContaining("比例");
        assertThatThrownBy(() -> service.normalize(new MockMultipartFile("photo", new byte[]{1, 2, 3})))
                .isInstanceOf(ResumeException.class).hasMessageContaining("图片");
    }

    @Test
    void acceptsExactlyFiveMegabytesAndRejectsLarger() throws Exception {
        byte[] exact = Arrays.copyOf(picture(400, 600, "png"), (int) ResumePhotoService.MAX_BYTES);
        assertThat(service.normalize(new MockMultipartFile("photo", exact))).isNotEmpty();
        assertThatThrownBy(() -> service.normalize(new MockMultipartFile("photo", new byte[exact.length + 1])))
                .isInstanceOf(ResumeException.class).hasMessageContaining("5MB");
    }

    @Test
    void readsJpegOrientationBeforeValidatingRatio() throws Exception {
        byte[] original = picture(900, 600, "jpeg");
        byte[] exif = HexFormat.of().parseHex("ffe1002245786966000049492a0008000000010012010300010000000600000000000000");
        var output = new ByteArrayOutputStream();
        output.write(original, 0, 2);
        output.write(exif);
        output.write(original, 2, original.length - 2);
        assertThat(service.normalize(new MockMultipartFile("photo", output.toByteArray()))).isNotEmpty();
    }
}
