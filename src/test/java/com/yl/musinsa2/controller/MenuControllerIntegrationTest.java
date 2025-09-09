package com.yl.musinsa2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.entity.GenderFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
class MenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("무신사 스타일 메뉴 API 테스트 - 전체 성별")
    void getMenu_withAllGender_shouldReturnMenuData() throws Exception {
        mockMvc.perform(get("/api2/dp/v4/menu")
                        .param("tabId", "category")
                        .param("gf", "A"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.tabs").isArray())
                .andExpect(jsonPath("$.data.gender").isArray())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    @DisplayName("무신사 스타일 메뉴 API 테스트 - 남성")
    void getMenu_withMaleGender_shouldReturnFilteredMenuData() throws Exception {
        mockMvc.perform(get("/api2/dp/v4/menu")
                        .param("tabId", "category")
                        .param("gf", "M"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    @DisplayName("무신사 스타일 메뉴 API 테스트 - 여성")
    void getMenu_withFemaleGender_shouldReturnFilteredMenuData() throws Exception {
        mockMvc.perform(get("/api2/dp/v4/menu")
                        .param("tabId", "category")
                        .param("gf", "F"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    @DisplayName("새로운 카테고리 생성 테스트 - 무신사 스타일 필드 포함")
    void createCategory_withMusinsaStyleFields_shouldCreateSuccessfully() throws Exception {
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("테스트 카테고리")
                .description("테스트용 카테고리")
                .code("TEST001")
                .groupTitle("테스트 그룹")
                .displayOrder(999)
                .genderFilter(GenderFilter.ALL)
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("테스트 카테고리"))
                .andExpect(jsonPath("$.description").value("테스트용 카테고리"));
    }

    @Test
    @DisplayName("기존 카테고리 API 호환성 테스트")
    void getAllCategories_shouldReturnAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("카테고리 트리 구조 테스트")
    void getCategoryTree_shouldReturnTreeStructure() throws Exception {
        mockMvc.perform(get("/api/categories/tree"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
