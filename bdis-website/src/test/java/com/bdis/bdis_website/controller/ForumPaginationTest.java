package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.service.ForumPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ForumPaginationTest {
    private MockMvc mvc;
    private ForumPostService forums;

    @BeforeEach
    void prepareController() {
        forums = mock(ForumPostService.class);
        var controller = new PlatformController();
        ReflectionTestUtils.setField(controller, "forumPostService", forums);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rejectsInvalidLimitsBeforeQuerying() throws Exception {
        for (String limit : new String[]{"-1", "0", "101", "2147483647"}) {
            mvc.perform(get("/api/platform/forum").param("limit", limit))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(1));
        }
        verifyNoInteractions(forums);
    }

    @Test
    void preservesDefaultAndExplicitValidLimits() throws Exception {
        when(forums.getForumPosts(null)).thenReturn(List.of());
        when(forums.getForumPosts(5)).thenReturn(List.of());
        mvc.perform(get("/api/platform/forum")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0));
        mvc.perform(get("/api/platform/forum").param("limit", "5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
        verify(forums).getForumPosts(null);
        verify(forums).getForumPosts(5);
    }
}
