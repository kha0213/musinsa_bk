package com.yl.musinsa2.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.entity.GenderFilter;
import com.yl.musinsa2.service.CategoryCacheInitializer;
import com.yl.musinsa2.service.CategoryCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CategoryController 통합테스트")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryCacheService categoryCache;

    @MockitoBean
    private CategoryCacheInitializer cacheInitializer;

    @Test
    @DisplayName("카테고리 트리 조회 API")
    void getCategoryTree() throws Exception {
        // given - 캐시가 비어있다고 가정
        when(categoryCache.getCategoryTree()).thenReturn(new ArrayList<>());

        // when & then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("특정 카테고리 조회 API - 존재하는 카테고리")
    void getCategoryById_Exists() throws Exception {
        // given - 캐시 미스 시나리오
        when(categoryCache.getCategory(1L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("특정 카테고리 조회 API - 존재하지 않는 카테고리")
    void getCategoryById_NotExists() throws Exception {
        // given
        when(categoryCache.getCategory(99999L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/categories/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리 생성 API - 성공")
    void createCategory_Success() throws Exception {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("새로운 카테고리")
                .description("새로운 카테고리 설명")
                .code("NEW001")
                .displayOrder(99)
                .genderFilter(GenderFilter.ALL)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("새로운 카테고리"))
                .andExpect(jsonPath("$.description").value("새로운 카테고리 설명"))
                .andExpect(jsonPath("$.code").value("NEW001"));
    }

    @Test
    @DisplayName("카테고리 생성 API - 유효성 검증 실패")
    void createCategory_ValidationFail() throws Exception {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("") // 빈 이름
                .description("설명")
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 수정 API - 성공")
    void updateCategory_Success() throws Exception {
        // given
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("수정된 카테고리")
                .description("수정된 설명")
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("수정된 카테고리"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));
    }

    @Test
    @DisplayName("카테고리 수정 API - 존재하지 않는 카테고리")
    void updateCategory_NotExists() throws Exception {
        // given
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("수정된 카테고리")
                .description("수정된 설명")
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(put("/api/categories/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리 삭제 API - 성공")
    void deleteCategory_Success() throws Exception {
        // given - 먼저 자식이 없는 카테고리를 생성
        CategoryCreateRequest createRequest = CategoryCreateRequest.builder()
                .name("삭제용 카테고리")
                .description("삭제될 카테고리")
                .code("DELETE_TEST")
                .build();

        String createJson = objectMapper.writeValueAsString(createRequest);

        String createResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 생성된 카테고리의 ID를 추출
        Long createdId = objectMapper.readTree(createResponse).get("id").asLong();

        // when - 삭제 요청
        mockMvc.perform(delete("/api/categories/" + createdId))
                .andExpect(status().isNoContent());

        // then - 삭제된 카테고리 조회 시 404 반환 확인
        when(categoryCache.getCategory(createdId)).thenReturn(null);

        mockMvc.perform(get("/api/categories/" + createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리 검색 API - 검색어 없음")
    void searchCategories_NoKeyword() throws Exception {
        // given
        when(categoryCache.getCategoryTree()).thenReturn(new ArrayList<>());

        // when & then
        mockMvc.perform(get("/api/categories/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("카테고리 검색 API - 검색어 있음")
    void searchCategories_WithKeyword() throws Exception {
        // given
        when(categoryCache.getCategoryTree()).thenReturn(new ArrayList<>());

        // when & then
        mockMvc.perform(get("/api/categories/search")
                        .param("name", "상의"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("특정 카테고리 하위 검색 API")
    void searchCategorySubTree() throws Exception {
        // given
        when(categoryCache.getCategory(1L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/categories/search/1")
                        .param("name", "티셔츠"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("캐시 갱신 API")
    void refreshCache() throws Exception {
        mockMvc.perform(post("/api/categories/cache/refresh"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 HTTP 메소드 사용")
    void wrongHttpMethod() throws Exception {
        mockMvc.perform(patch("/api/categories/1"))
                .andExpect(status().isMethodNotAllowed());
    }
}
