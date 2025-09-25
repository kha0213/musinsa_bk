package com.yl.musinsa2.unit.service;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryDto;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import com.yl.musinsa2.repository.CategoryRepository;
import com.yl.musinsa2.service.CategoryCacheInitializer;
import com.yl.musinsa2.service.CategoryCacheService;
import com.yl.musinsa2.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 단위테스트")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryCacheService categoryCache;

    @Mock
    private CategoryCacheInitializer cacheInitializer;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryDto testCategoryDto;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .code("TEST001")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .children(new ArrayList<>())
                .build();

        testCategoryDto = CategoryDto.builder()
                .id(1L)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .code("TEST001")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .children(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("카테고리 ID로 조회 - 캐시에서 조회 성공")
    void getCategoryById_FromCache_Success() {
        // given
        when(categoryCache.getCategory(1L)).thenReturn(testCategoryDto);
        when(categoryCache.getChildCategories(1L)).thenReturn(new ArrayList<>());

        // when
        CategoryResponse result = categoryService.getCategoryById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 카테고리");
        verify(categoryCache).getCategory(1L);
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("카테고리 ID로 조회 - 캐시 미스 시 DB에서 조회")
    void getCategoryById_CacheMiss_FromDatabase() {
        // given
        when(categoryCache.getCategory(1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // when
        CategoryResponse result = categoryService.getCategoryById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 카테고리");
        verify(categoryCache).getCategory(1L);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("카테고리 ID로 조회 - 존재하지 않는 카테고리")
    void getCategoryById_NotFound_ThrowsException() {
        // given
        when(categoryCache.getCategory(999L)).thenReturn(null);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다. ID: 999");
    }

    @Test
    @DisplayName("카테고리 생성 - 부모 카테고리 없는 루트 카테고리")
    void createCategory_RootCategory_Success() {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("새 카테고리")
                .description("새 설명")
                .code("NEW001")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .build();

        Category savedCategory = Category.builder()
                .id(2L)
                .name("새 카테고리")
                .description("새 설명")
                .code("NEW001")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // when
        CategoryResponse result = categoryService.createCategory(request);

        // then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("새 카테고리");
        assertThat(result.isRoot()).isTrue();
        verify(categoryRepository).save(any(Category.class));
        verify(categoryCache).updateCategory(any(CategoryDto.class), isNull());
    }

    @Test
    @DisplayName("카테고리 생성 - 부모 카테고리가 있는 자식 카테고리")
    void createCategory_ChildCategory_Success() {
        // given
        Category parentCategory = Category.builder()
                .id(1L)
                .name("부모 카테고리")
                .build();

        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("자식 카테고리")
                .description("자식 설명")
                .parentId(1L)
                .build();

        Category savedCategory = Category.builder()
                .id(2L)
                .name("자식 카테고리")
                .description("자식 설명")
                .parent(parentCategory)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // when
        CategoryResponse result = categoryService.createCategory(request);

        // then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("자식 카테고리");
        assertThat(result.getParentId()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 존재하지 않는 부모 카테고리")
    void createCategory_ParentNotFound_ThrowsException() {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("자식 카테고리")
                .parentId(999L)
                .build();

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("부모 카테고리를 찾을 수 없습니다. ID: 999");
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory_Success() {
        // given
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("수정된 카테고리")
                .description("수정된 설명")
                .build();

        Category updatedCategory = Category.builder()
                .id(1L)
                .name("수정된 카테고리")
                .description("수정된 설명")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // when
        CategoryResponse result = categoryService.updateCategory(1L, request);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("수정된 카테고리");
        assertThat(result.getDescription()).isEqualTo("수정된 설명");
        verify(categoryCache).updateCategory(any(CategoryDto.class), isNull());
    }

    @Test
    @DisplayName("카테고리 삭제 - 자식이 없는 카테고리 삭제 성공")
    void deleteCategory_NoChildren_Success() {
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // when
        categoryService.deleteCategory(1L);

        // then
        verify(categoryRepository).delete(testCategory);
        verify(categoryCache).removeCategory(any(CategoryDto.class));
    }

    @Test
    @DisplayName("카테고리 삭제 - 자식이 있는 카테고리 삭제 실패")
    void deleteCategory_HasChildren_ThrowsException() {
        // given
        Category childCategory = Category.builder()
                .id(2L)
                .name("자식 카테고리")
                .build();

        testCategory.addChild(childCategory);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("캐시 갱신 - 성공")
    void refreshCache_Success() {
        // when
        categoryService.refreshCache();

        // then
        verify(cacheInitializer).reinitializeCache();
    }

    @Test
    @DisplayName("카테고리 트리 조회 - 캐시에서 조회")
    void getCategoryTree_FromCache() {
        // given
        List<CategoryDto> rootCategories = List.of(testCategoryDto);
        when(categoryCache.getCategoryTree()).thenReturn(rootCategories);

        // when
        List<CategoryResponse> result = categoryService.getCategoryTree();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 카테고리");
        verify(categoryCache).getCategoryTree();
    }

    @Test
    @DisplayName("카테고리 트리 조회 - 캐시 미스 시 DB에서 조회")
    void getCategoryTree_CacheMiss_FromDatabase() {
        // given
        when(categoryCache.getCategoryTree()).thenReturn(new ArrayList<>());
        when(categoryCache.loadAndCacheFromDB()).thenReturn(List.of(testCategoryDto));

        // when
        List<CategoryResponse> result = categoryService.getCategoryTree();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 카테고리");
        verify(categoryCache).loadAndCacheFromDB();
    }
}
