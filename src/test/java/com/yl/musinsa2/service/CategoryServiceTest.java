package com.yl.musinsa2.service;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryStatistics;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 테스트")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category rootCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        rootCategory = Category.builder()
                .id(1L)
                .name("루트 카테고리")
                .description("루트 카테고리 설명")
                .build();

        childCategory = Category.builder()
                .id(2L)
                .name("자식 카테고리")
                .description("자식 카테고리 설명")
                .parent(rootCategory)
                .build();
        
        log.debug("테스트 데이터 준비 완료");
    }

    @Test
    @DisplayName("모든 카테고리 조회 테스트")
    void getAllCategories_Success() {
        // given
        List<Category> categories = Arrays.asList(rootCategory, childCategory);
        given(categoryRepository.findByIsActiveTrue()).willReturn(categories);

        // when
        List<CategoryResponse> result = categoryService.getAllCategories();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("루트 카테고리");
        assertThat(result.get(1).getName()).isEqualTo("자식 카테고리");
        
        log.info("모든 카테고리 조회 테스트 성공");
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    void createCategory_Success() {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("새 카테고리")
                .description("새 카테고리 설명")
                .build();
        
        Category savedCategory = Category.builder()
                .id(3L)
                .name("새 카테고리")
                .description("새 카테고리 설명")
                .build();

        given(categoryRepository.save(any(Category.class))).willReturn(savedCategory);

        // when
        CategoryResponse result = categoryService.createCategory(request);

        // then
        assertThat(result.getName()).isEqualTo("새 카테고리");
        assertThat(result.getDescription()).isEqualTo("새 카테고리 설명");
        verify(categoryRepository, times(1)).save(any(Category.class));
        
        log.info("카테고리 생성 테스트 성공");
    }

    @Test
    @DisplayName("부모 카테고리와 함께 카테고리 생성 테스트")
    void createCategoryWithParent_Success() {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("자식 카테고리")
                .description("자식 카테고리 설명")
                .parentId(1L)
                .build();
        
        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(childCategory);

        // when
        CategoryResponse result = categoryService.createCategory(request);

        // then
        assertThat(result.getName()).isEqualTo("자식 카테고리");
        assertThat(result.getParentId()).isEqualTo(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
        
        log.info("부모 카테고리와 함께 카테고리 생성 테스트 성공");
    }

    @Test
    @DisplayName("존재하지 않는 부모로 카테고리 생성 실패 테스트")
    void createCategoryWithNonExistentParent_Fail() {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("자식 카테고리")
                .description("자식 카테고리 설명")
                .parentId(999L)
                .build();
        
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("부모 카테고리를 찾을 수 없습니다");
        
        log.info("존재하지 않는 부모로 카테고리 생성 실패 테스트 성공");
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    void updateCategory_Success() {
        // given
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("수정된 카테고리")
                .description("수정된 설명")
                .isActive(true)
                .build();
        
        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(rootCategory);

        // when
        CategoryResponse result = categoryService.updateCategory(1L, request);

        // then
        assertThat(result.getName()).isEqualTo("수정된 카테고리");
        verify(categoryRepository, times(1)).save(any(Category.class));
        
        log.info("카테고리 수정 테스트 성공");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 수정 실패 테스트")
    void updateNonExistentCategory_Fail() {
        // given
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("수정된 카테고리")
                .description("수정된 설명")
                .isActive(true)
                .build();
        
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(999L, request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("카테고리를 찾을 수 없습니다");
        
        log.info("존재하지 않는 카테고리 수정 실패 테스트 성공");
    }

    @Test
    @DisplayName("자식이 없는 카테고리 삭제 성공 테스트")
    void deleteCategory_Success() {
        // given
        given(categoryRepository.findById(2L)).willReturn(Optional.of(childCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(childCategory);

        // when
        categoryService.deleteCategory(2L);

        // then
        verify(categoryRepository, times(1)).save(any(Category.class));
        
        log.info("자식이 없는 카테고리 삭제 성공 테스트 성공");
    }

    @Test
    @DisplayName("자식이 있는 카테고리 삭제 실패 테스트")
    void deleteCategoryWithChildren_Fail() {
        // given
        rootCategory.addChild(childCategory);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다");
        
        log.info("자식이 있는 카테고리 삭제 실패 테스트 성공");
    }

    @Test
    @DisplayName("카테고리 검색 테스트")
    void searchCategoriesByName_Success() {
        // given
        List<Category> searchResult = Arrays.asList(rootCategory);
        given(categoryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("루트")).willReturn(searchResult);

        // when
        List<CategoryResponse> result = categoryService.searchCategoriesByName("루트");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("루트 카테고리");
        
        log.info("카테고리 검색 테스트 성공");
    }

    @Test
    @DisplayName("카테고리 상태 토글 테스트")
    void toggleCategoryStatus_Success() {
        // given
        rootCategory.setIsActive(true);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(rootCategory);

        // when
        CategoryResponse result = categoryService.toggleCategoryStatus(1L);

        // then
        assertThat(result.getIsActive()).isFalse();
        verify(categoryRepository, times(1)).save(any(Category.class));
        
        log.info("카테고리 상태 토글 테스트 성공");
    }

    @Test
    @DisplayName("카테고리 통계 조회 테스트")
    void getCategoryStatistics_Success() {
        // given
        given(categoryRepository.countByIsActiveTrue()).willReturn(10L);
        given(categoryRepository.findByParentIsNullAndIsActiveTrue()).willReturn(Arrays.asList(rootCategory));

        // when
        CategoryStatistics result = categoryService.getCategoryStatistics();

        // then
        assertThat(result.getTotalCategories()).isEqualTo(10L);
        assertThat(result.getRootCategories()).isEqualTo(1L);
        assertThat(result.getSubCategories()).isEqualTo(9L);
        
        log.info("카테고리 통계 조회 테스트 성공");
    }
}
