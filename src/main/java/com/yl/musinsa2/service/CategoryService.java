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
     * 모든 카테고리 조회 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.debug("모든 카테고리 조회 시작 (캐시 우선)");

        // 캐시가 비어있다면 초기화
        if (categoryCache.isCacheEmpty()) {
            log.info("캐시가 비어있음. 캐시 초기화 실행");
            cacheInitializer.reinitializeCache();
        }

        List<CategoryDto> categories = categoryCache.getAllActiveCategories();

        if (categories.isEmpty()) {
            log.warn("캐시에서 카테고리를 찾을 수 없음. DB에서 직접 조회");
            List<Category> dbCategories = categoryRepository.findAll();
            return dbCategories.stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList());
        }

        log.debug("캐시에서 조회된 카테고리 수: {}", categories.size());

        return categories.stream()
                .map(CategoryResponse::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 루트 카테고리들만 조회 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        log.debug("루트 카테고리 조회 시작 (캐시 우선)");

        // 캐시가 비어있다면 초기화
        if (categoryCache.isCacheEmpty()) {
            log.info("캐시가 비어있음. 캐시 초기화 실행");
            cacheInitializer.reinitializeCache();
        }

        List<CategoryDto> rootCategories = categoryCache.getCategoryTree();

        if (rootCategories.isEmpty()) {
            log.warn("캐시에서 루트 카테고리를 찾을 수 없음. DB에서 직접 조회");
            List<Category> dbRootCategories = categoryRepository.findByParentIsNull();
            return dbRootCategories.stream()
                    .map(CategoryResponse::fromWithChildren)
                    .collect(Collectors.toList());
        }

        log.debug("캐시에서 조회된 루트 카테고리 수: {}", rootCategories.size());

        return rootCategories.stream()
                .map(CategoryResponse::convertToResponseWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리 조회 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.debug("카테고리 조회 시작, ID: {} (캐시 우선)", id);

        CategoryDto categoryDto = categoryCache.getCategory(id);

        if (categoryDto == null) {
            log.debug("캐시에서 카테고리를 찾을 수 없음. DB에서 조회: {}", id);
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
            return CategoryResponse.fromWithChildren(category);
        }

        // 자식 카테고리들도 함께 조회
        List<CategoryDto> children = categoryCache.getChildCategories(id);
        categoryDto.setChildren(children);

        log.debug("캐시에서 카테고리 조회 완료: {}", categoryDto.getName());
        return CategoryResponse.convertToResponseWithChildren(categoryDto);
    }

    /**
     * 카테고리 생성 (DB + 캐시 업데이트)
     */
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        log.debug("카테고리 생성 시작: {}", request.getName());

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

        // 부모 카테고리가 지정된 경우
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 카테고리를 찾을 수 없습니다. ID: " + request.getParentId()));
            category.setParent(parent);
            log.debug("부모 카테고리 설정: {}", parent.getName());
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("카테고리 DB 저장 완료: {}", savedCategory.getName());

        // 캐시에 추가
        CategoryDto categoryDto = CategoryDto.from(savedCategory);
        categoryCache.addCategory(categoryDto);
        log.info("카테고리 캐시 추가 완료: {}", savedCategory.getName());

        return CategoryResponse.from(savedCategory);
    }

    /**
     * 카테고리 수정 (DB + 캐시 업데이트)
     */
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        log.debug("카테고리 수정 시작, ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        Long oldParentId = category.getParent() != null ? category.getParent().getId() : null;

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        log.info("카테고리 DB 수정 완료: {}", updatedCategory.getName());

        // 캐시 업데이트
        CategoryDto categoryDto = CategoryDto.from(updatedCategory);
        categoryCache.updateCategory(categoryDto, oldParentId);
        log.info("카테고리 캐시 수정 완료: {}", updatedCategory.getName());

        return CategoryResponse.from(updatedCategory);
    }

    /**
     * 카테고리 삭제
     */
    public void deleteCategory(Long id) {
        log.debug("카테고리 삭제 시작, ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        // 자식 카테고리가 있는 경우 삭제 불가
        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");
        }

        CategoryDto categoryDto = CategoryDto.from(category);

        // Hibernate @SoftDelete가 자동으로 deleted=true로 설정
        categoryRepository.delete(category);
        log.info("카테고리 삭제 완료: {}", category.getName());

        // 캐시에서 제거
        categoryCache.removeCategory(categoryDto);
        log.info("카테고리 캐시 삭제 완료: {}", category.getName());
    }

    /**
     * 카테고리 이름으로 검색 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoriesByName(String name) {
        log.debug("카테고리 검색 시작, 키워드: {} (캐시 우선)", name);

        // 캐시에서 모든 카테고리 조회 후 필터링
        List<CategoryDto> allCategories = categoryCache.getAllActiveCategories();

        if (allCategories.isEmpty()) {
            log.warn("캐시에서 카테고리를 찾을 수 없음. DB에서 직접 검색");
            List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);
            return categories.stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList());
        }

        // 캐시된 카테고리에서 이름으로 필터링
        List<CategoryDto> filteredCategories = allCategories.stream()
                .filter(category -> category.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();

        log.debug("캐시에서 검색 결과 수: {}", filteredCategories.size());

        return filteredCategories.stream()
                .map(CategoryResponse::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 전체 카테고리 트리 검색 (캐시 우선)
     * name이 null이면 전체 트리 반환, 값이 있으면 필터링된 트리 반환
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoriesTree(String name) {
        log.debug("전체 카테고리 트리 검색 시작, 키워드: {} (캐시 우선)", name);

        // 캐시에서 트리 구조 조회
        List<CategoryDto> rootCategories = categoryCache.getCategoryTree();

        if (rootCategories.isEmpty()) {
            log.warn("캐시에서 카테고리를 찾을 수 없음. DB에서 직접 조회");
            List<Category> dbRootCategories = categoryRepository.findByParentIsNull();
            List<CategoryResponse> treeFromDb = dbRootCategories.stream()
                    .map(CategoryResponse::fromWithChildren)
                    .collect(Collectors.toList());

            if (name != null && !name.trim().isEmpty()) {
                return filterCategoryTree(treeFromDb, name);
            }
            return treeFromDb;
        }

        // 캐시에서 가져온 트리 구조를 CategoryResponse로 변환
        List<CategoryResponse> tree = rootCategories.stream()
                .map(CategoryResponse::convertToResponseWithChildren)
                .collect(Collectors.toList());

        // 검색어가 있으면 필터링
        if (name != null && !name.trim().isEmpty()) {
            tree = filterCategoryTree(tree, name);
        }

        log.debug("전체 카테고리 트리 검색 결과 수: {}", tree.size());
        return tree;
    }

    /**
     * 특정 카테고리 하위 트리 검색 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public CategoryResponse searchCategorySubTree(Long categoryId, String name) {
        log.debug("특정 카테고리 하위 트리 검색 시작, ID: {}, 키워드: {} (캐시 우선)", categoryId, name);

        // 먼저 기준 카테고리 조회
        CategoryResponse baseCategory = getCategoryById(categoryId);

        // 검색어가 없으면 해당 카테고리와 모든 하위 카테고리 반환
        if (name == null || name.trim().isEmpty()) {
            return baseCategory;
        }

        // 검색어가 있으면 해당 카테고리부터 시작하여 필터링
        CategoryResponse filteredCategory = filterSingleCategoryTree(baseCategory, name);

        log.debug("특정 카테고리 하위 트리 검색 완료: {}", filteredCategory.getName());
        return filteredCategory;
    }

    /**
     * 카테고리 트리에서 이름으로 필터링
     */
    private List<CategoryResponse> filterCategoryTree(List<CategoryResponse> categories, String name) {
        return categories.stream()
                .map(category -> filterSingleCategoryTree(category, name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 단일 카테고리 트리에서 이름으로 필터링 (재귀적)
     */
    private CategoryResponse filterSingleCategoryTree(CategoryResponse category, String name) {
        // 현재 카테고리가 검색 조건에 맞는지 확인
        boolean currentMatches = category.getName().toLowerCase().contains(name.toLowerCase());

        // 자식 카테고리들 필터링
        List<CategoryResponse> filteredChildren = null;
        if (category.getChildren() != null) {
            filteredChildren = category.getChildren().stream()
                    .map(child -> filterSingleCategoryTree(child, name))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 현재 카테고리가 매치되거나 하위에 매치되는 카테고리가 있으면 포함
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

        return null; // 조건에 맞지 않으면 제외
    }

    /**
     * 전체 카테고리 트리 구조 조회 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        log.debug("카테고리 트리 조회 시작 (캐시 우선)");
        return getRootCategories(); // 루트부터 시작하여 자식들까지 모두 포함
    }

    /**
     * 캐시 수동 재초기화
     */
    public void refreshCache() {
        log.info("카테고리 캐시 수동 재초기화 요청");
        cacheInitializer.reinitializeCache();
    }
}
