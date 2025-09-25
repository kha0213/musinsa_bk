package com.yl.musinsa2.service;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryDto;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCacheService categoryCache;
    private final CategoryCacheInitializer cacheInitializer;

    /**
     * 개별 카테고리 조회 - category:id 사용
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        // 1. 개별 캐시에서 조회
        CategoryDto categoryDto = categoryCache.getCategory(id);

        if (categoryDto != null) {
            List<CategoryDto> children = categoryCache.getChildCategories(id);
            categoryDto.setChildren(children);
            return CategoryResponse.convertToResponseWithChildren(categoryDto);
        }

        // 2. 캐시 미스 시 DB에서 조회
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        CategoryResponse response = CategoryResponse.fromWithChildren(category);

        // 3. 캐시에 저장
        CategoryDto dto = CategoryDto.from(category);
        categoryCache.addCategory(dto);

        return response;
    }

    /**
     * 전체 카테고리 트리 조회 - category:tree 사용
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        // 1. 트리 캐시에서 조회
        List<CategoryDto> tree = categoryCache.getCategoryTree();

        if (!tree.isEmpty()) {
            return tree.stream()
                    .map(CategoryResponse::convertToResponseWithChildren)
                    .collect(Collectors.toList());
        }

        // 2. 캐시 미스 시 DB에서 로딩
        List<CategoryDto> loadedTree = categoryCache.loadAndCacheFromDB();
        return loadedTree.stream()
                .map(CategoryResponse::convertToResponseWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 검색 - 트리에서 필터링
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoriesTree(String name) {
        List<CategoryResponse> tree = getCategoryTree();

        if (name == null || name.trim().isEmpty()) {
            return tree;
        }

        return filterCategoryTree(tree, name);
    }

    /**
     * 특정 카테고리의 서브트리 검색
     */
    @Transactional(readOnly = true)
    public CategoryResponse searchCategorySubTree(Long categoryId, String name) {
        CategoryResponse baseCategory = getCategoryById(categoryId);

        if (name == null || name.trim().isEmpty()) {
            return baseCategory;
        }

        return filterSingleCategoryTree(baseCategory, name);
    }

    /**
     * 카테고리 생성 - 양쪽 캐시 업데이트
     */
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .storeCode(request.getStoreCode())
                .storeTitle(request.getStoreTitle())
                .groupTitle(request.getGroupTitle())
                .displayOrder(request.getDisplayOrder())
                .genderFilter(request.getGenderFilter())
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 카테고리를 찾을 수 없습니다. ID: " + request.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        CategoryResponse response = CategoryResponse.from(savedCategory);

        // 양쪽 캐시 업데이트
        CategoryDto categoryDto = CategoryDto.from(savedCategory);
        categoryCache.updateCategory(categoryDto, null);

        return response;
    }

    /**
     * 카테고리 수정 - 양쪽 캐시 업데이트
     */
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        Long oldParentId = category.getParent() != null ? category.getParent().getId() : null;

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        CategoryResponse response = CategoryResponse.from(updatedCategory);

        // 양쪽 캐시 업데이트
        CategoryDto categoryDto = CategoryDto.from(updatedCategory);
        categoryCache.updateCategory(categoryDto, oldParentId);

        return response;
    }

    /**
     * 카테고리 삭제 - 양쪽 캐시에서 제거
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");
        }

        CategoryDto categoryDto = CategoryDto.from(category);

        categoryRepository.delete(category);

        // 양쪽 캐시에서 제거
        categoryCache.removeCategory(categoryDto);
    }

    /**
     * 캐시 수동 갱신
     */
    public void refreshCache() {
        cacheInitializer.reinitializeCache();
    }

    /**
     * 트리에서 카테고리 필터링
     */
    private List<CategoryResponse> filterCategoryTree(List<CategoryResponse> categories, String name) {
        return categories.stream()
                .map(category -> filterSingleCategoryTree(category, name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 단일 카테고리 트리 필터링
     */
    private CategoryResponse filterSingleCategoryTree(CategoryResponse category, String name) {
        boolean currentMatches = category.getName().toLowerCase().contains(name.toLowerCase());

        List<CategoryResponse> filteredChildren = null;
        if (category.getChildren() != null) {
            filteredChildren = category.getChildren().stream()
                    .map(child -> filterSingleCategoryTree(child, name))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        if (currentMatches || (filteredChildren != null && !filteredChildren.isEmpty())) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .code(category.getCode())
                    .storeCode(category.getStoreCode())
                    .storeTitle(category.getStoreTitle())
                    .groupTitle(category.getGroupTitle())
                    .displayOrder(category.getDisplayOrder())
                    .genderFilter(category.getGenderFilter())
                    .parentId(category.getParentId())
                    .parentName(category.getParentName())
                    .createdAt(category.getCreatedAt())
                    .updatedAt(category.getUpdatedAt())
                    .isRoot(category.isRoot())
                    .leaf(filteredChildren == null || filteredChildren.isEmpty())
                    .children(filteredChildren)
                    .build();
        }

        return null;
    }
}
