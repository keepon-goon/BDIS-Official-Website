package com.bdis.bdis_website.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.entity.NewsItem;
import com.bdis.bdis_website.entity.AnnouncementItem;
import com.bdis.bdis_website.service.AnnouncementItemService;
import com.bdis.bdis_website.service.EducationResourceService;
import com.bdis.bdis_website.service.NewsItemService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {
    @Autowired MockMvc mvc;
    @MockBean NewsItemService news;
    @MockBean AnnouncementItemService announcements;
    @MockBean EducationResourceService resources;

    @Test
    void rejectsInvalidPaginationBeforeQuerying() throws Exception {
        for (String[] values : new String[][]{
                {"0", "10"}, {"-1", "10"}, {"100001", "10"}, {"2147483647", "100"},
                {"1", "0"}, {"1", "-1"}, {"1", "101"}, {"1", "2147483647"}
        }) {
            mvc.perform(get("/api/search").param("q", "测试").param("page", values[0]).param("limit", values[1]))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(1));
        }
        verifyNoInteractions(news, announcements, resources);
    }

    @Test
    void keepsDefaultPaginationAndSearchResultShape() throws Exception {
        when(news.list(ArgumentMatchers.<QueryWrapper<NewsItem>>any())).thenReturn(List.of(item(1L), item(2L), item(3L)));
        mvc.perform(get("/api/search").param("q", " 测试 "))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.page").value(1)).andExpect(jsonPath("$.data.limit").value(10))
                .andExpect(jsonPath("$.data.list.length()").value(3))
                .andExpect(jsonPath("$.data.list[0].link").value("/info/news/1"));
    }

    @Test
    void returnsTheRequestedPageAndAnEmptyOutOfRangePage() throws Exception {
        when(news.list(ArgumentMatchers.<QueryWrapper<NewsItem>>any())).thenReturn(List.of(item(1L), item(2L), item(3L)));
        mvc.perform(get("/api/search").param("q", "测试").param("page", "2").param("limit", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].id").value(3));
        mvc.perform(get("/api/search").param("q", "测试").param("page", "100000").param("limit", "100"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    void keepsNewsAndAnnouncementLinksDistinctWhenIdsOverlap() throws Exception {
        var announcement = new AnnouncementItem();
        announcement.setId(1L);
        announcement.setTitle("测试公告");
        when(news.list(ArgumentMatchers.<QueryWrapper<NewsItem>>any())).thenReturn(List.of(item(1L)));
        when(announcements.list(ArgumentMatchers.<QueryWrapper<AnnouncementItem>>any())).thenReturn(List.of(announcement));
        mvc.perform(get("/api/search").param("q", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].type").value("news"))
                .andExpect(jsonPath("$.data.list[0].link").value("/info/news/1"))
                .andExpect(jsonPath("$.data.list[1].type").value("announcement"))
                .andExpect(jsonPath("$.data.list[1].link").value("/info/announcements/1"));
    }

    @Test
    void keepsBlankKeywordValidationAndRejectsNonNumericPagination() throws Exception {
        mvc.perform(get("/api/search").param("q", " "))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(1));
        mvc.perform(get("/api/search").param("q", "测试").param("page", "abc"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(news, announcements, resources);
    }

    private NewsItem item(long id) {
        var item = new NewsItem();
        item.setId(id);
        item.setTitle("测试新闻" + id);
        return item;
    }
}
