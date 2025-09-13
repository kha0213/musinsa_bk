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

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        CategoryDto categoryDto = categoryCache.getCategory(id);

        if (categoryDto == null) {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
            return CategoryResponse.fromWithChildren(category);
        }

        List<CategoryDto> children = categoryCache.getChildCategories(id);
        categoryDto.setChildren(children);

        return CategoryResponse.convertToResponseWithChildren(categoryDto);
    }

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

        CategoryDto categoryDto = CategoryDto.from(savedCategory);
        categoryCache.addCategory(categoryDto);

        return CategoryResponse.from(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        Long oldParentId = category.getParent() != null ? category.getParent().getId() : null;

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        CategoryDto categoryDto = CategoryDto.from(updatedCategory);
        categoryCache.updateCategory(categoryDto, oldParentId);

        return CategoryResponse.from(updatedCategory);
    }

    // Hibernate @SoftDelete 자동 처리
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");
        }

        CategoryDto categoryDto = CategoryDto.from(category);

        categoryRepository.delete(category);
        categoryCache.removeCategory(categoryDto);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoriesTree(String name) {
        List<CategoryDto> rootCategories = categoryCache.getCategoryTree();

        if (rootCategories.isEmpty()) {
            List<Category> dbRootCategories = categoryRepository.findByParentIsNull();
            List<CategoryResponse> treeFromDb = dbRootCategories.stream()
                    .map(CategoryResponse::fromWithChildren)
                    .collect(Collectors.toList());

            if (name != null && !name.trim().isEmpty()) {
                return filterCategoryTree(treeFromDb, name);
            }
            return treeFromDb;
        }

        List<CategoryResponse> tree = rootCategories.stream()
                .map(CategoryResponse::convertToResponseWithChildren)
                .collect(Collectors.toList());

        if (name != null && !name.trim().isEmpty()) {
            tree = filterCategoryTree(tree, name);
        }

        return tree;
    }

    @Transactional(readOnly = true)
    public CategoryResponse searchCategorySubTree(Long categoryId, String name) {
        CategoryResponse baseCategory = getCategoryById(categoryId);

        if (name == null || name.trim().isEmpty()) {
            return baseCategory;
        }

        return filterSingleCategoryTree(baseCategory, name);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        return searchCategoriesTree(null);
    }

    public void refreshCache() {
        cacheInitializer.reinitializeCache();
    }

    private List<CategoryResponse> filterCategoryTree(List<CategoryResponse> categories, String name) {
        return categories.stream()
                .map(category -> filterSingleCategoryTree(category, name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

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
