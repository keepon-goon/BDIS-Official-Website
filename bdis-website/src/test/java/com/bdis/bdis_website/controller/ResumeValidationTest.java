package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.dto.ResumeUploadDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResumeValidationTest {
    @Autowired private Validator validator;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;

    @Test
    void acceptsCommonPhoneFormatsAndRejectsInvalidNumbers() {
        var form = new ResumeFormDTO();
        var upload = new ResumeUploadDTO();
        for (String phone : new String[]{"13800000000", "+86 13912345678", "023-62561234", "01088888888-123"}) {
            form.setPhone(phone);
            upload.setPhone(phone);
            assertThat(validator.validateProperty(form, "phone")).isEmpty();
            assertThat(validator.validateProperty(upload, "phone")).isEmpty();
        }
        for (String phone : new String[]{"123", "12800000000", "1380000000a", "<script>", ""}) {
            form.setPhone(phone);
            upload.setPhone(phone);
            assertThat(validator.validateProperty(form, "phone")).isNotEmpty();
            assertThat(validator.validateProperty(upload, "phone")).isNotEmpty();
        }
        for (String gender : new String[]{"男", "女", "沃尔玛购物袋", "武装直升机"}) {
            form.setGender(gender);
            assertThat(validator.validateProperty(form, "gender")).isEmpty();
        }
        form.setGender("其他");
        assertThat(validator.validateProperty(form, "gender")).isNotEmpty();
    }

    @Test
    void bothPreviewEntrancesRejectInvalidPhoneBeforeConversion() throws Exception {
        var form = json.readTree(new ClassPathResource("resume-form.json").getContentAsByteArray());
        ((com.fasterxml.jackson.databind.node.ObjectNode) form).put("phone", "123");
        mvc.perform(post("/api/resume/previews").contentType("application/json").content(json.writeValueAsBytes(form)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("请填写有效的手机号或带区号的固定电话"));
        mvc.perform(multipart("/api/resume/submit").file(new MockMultipartFile("resume", "test.docx", "application/octet-stream", new byte[]{1}))
                        .param("name", "测试").param("studentNo", "001").param("phone", "123"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("请填写有效的手机号或带区号的固定电话"));
    }
}
