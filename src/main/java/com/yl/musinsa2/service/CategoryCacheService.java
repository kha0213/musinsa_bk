package com.yl.musinsa2.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl.musinsa2.dto.CategoryDto;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    @Qualifier("categoryRedisTemplate")
    private final RedisTemplate<String, Object> categoryRedisTemplate;

    @Qualifier("treeRedisTemplate")
    private final RedisTemplate<String, Object> treeRedisTemplate;

    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    // 1. 개별 카테고리 키
    private static final String CATEGORY_KEY_PREFIX = "category:";

    // 2. 전체 트리 키
    private static final String CATEGORY_TREE_KEY = "category:tree";

    private static final long TTL_HOURS = 1;

    /**
     * 1. 개별 카테고리 조회 - category:id 형태
     */
    public CategoryDto getCategory(Long id) {
        String key = CATEGORY_KEY_PREFIX + id;

        try {
            Object categoryData = categoryRedisTemplate.opsForValue().get(key);
            if (categoryData != null) {
                CategoryResponse category = objectMapper.convertValue(categoryData, CategoryResponse.class);
                log.debug("개별 카테고리 캐시 HIT: id={}, name={}", id, category.getName());
                return convertToCategoryDto(category);
            }

            log.debug("개별 카테고리 캐시 MISS: id={}", id);
            return null;

        } catch (Exception e) {
            log.error("개별 카테고리 조회 실패: id={}, error={}", id, e.getMessage());
            return null;
        }
    }

    /**
     * 2. 전체 카테고리 트리 조회 - category:tree
     */
    public List<CategoryDto> getCategoryTree() {
        try {
            Object cachedTree = treeRedisTemplate.opsForValue().get(CATEGORY_TREE_KEY);

            if (cachedTree != null) {
                List<CategoryResponse> tree = objectMapper.convertValue(cachedTree,
                        new TypeReference<>() {
                        });

                log.debug("전체 트리 캐시 HIT: {} 루트 카테고리", tree.size());
                return tree.stream()
                        .map(this::convertToCategoryDto)
                        .collect(Collectors.toList());
            }

            log.debug("전체 트리 캐시 MISS");
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("전체 트리 조회 실패: error={}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 개별 카테고리 저장
     */
    public void addCategory(CategoryDto category) {
        String key = CATEGORY_KEY_PREFIX + category.getId();

        try {
            CategoryResponse response = convertToCategoryResponse(category);
            categoryRedisTemplate.opsForValue().set(key, response, Duration.ofHours(TTL_HOURS));
            log.debug("개별 카테고리 저장: id={}, name={}", category.getId(), category.getName());

        } catch (Exception e) {
            log.error("개별 카테고리 저장 실패: id={}, error={}", category.getId(), e.getMessage(), e);
        }
    }

    /**
     * 전체 트리 저장
     */
    public void saveCategoryTree(List<CategoryDto> tree) {
        try {
            List<CategoryResponse> responseTree = tree.stream()
                    .map(this::convertToCategoryResponse)
                    .collect(Collectors.toList());

            treeRedisTemplate.opsForValue().set(CATEGORY_TREE_KEY, responseTree, Duration.ofMinutes(30));
            log.debug("전체 트리 저장: {} 루트 카테고리", tree.size());

        } catch (Exception e) {
            log.error("전체 트리 저장 실패: error={}", e.getMessage());
        }
    }

    /**
     * DB에서 데이터 로딩 후 양쪽 모두에 캐싱
     */
    public List<CategoryDto> loadAndCacheFromDB() {
        try {
            log.info("DB에서 카테고리 데이터 로딩 시작");

            // 1. DB에서 모든 카테고리 조회
            List<Category> allCategories = categoryRepository.findAll();

            // 2. 트리 구조 구성
            List<CategoryDto> tree = buildCategoryTree(allCategories);

            // 3. 양쪽 캐시에 모두 저장
            cacheBothFormats(tree, allCategories);

            log.info("DB 로딩 및 캐싱 완료: {} 카테고리", allCategories.size());
            return tree;

        } catch (Exception e) {
            log.error("DB 로딩 실패", e);
            return Collections.emptyList();
        }
    }

    /**
     * 양쪽 형태로 모두 캐싱
     */
    private void cacheBothFormats(List<CategoryDto> tree, List<Category> allCategories) {
        // 1) 전체 트리 저장
        saveCategoryTree(tree);

        // 2) 개별 카테고리들을 일괄 저장 (Pipeline 사용)
        saveCategoriesBatch(allCategories);

        log.info("양쪽 캐시 저장 완료: 트리 + 개별({} 개)", allCategories.size());
    }

    /**
     * 개별 카테고리들을 일괄 저장 (MultiSet 사용)
     */
    private void saveCategoriesBatch(List<Category> allCategories) {
        try {
            // 일괄 저장용 Map 준비
            Map<String, Object> categoryMap = new HashMap<>();

            for (Category category : allCategories) {
                try {
                    String key = CATEGORY_KEY_PREFIX + category.getId();
                    CategoryResponse response = CategoryResponse.from(category);
                    categoryMap.put(key, response);
                } catch (Exception e) {
                    log.error("카테고리 변환 실패: id={}", category.getId(), e);
                }
            }

            if (!categoryMap.isEmpty()) {
                // MultiSet으로 일괄 저장
                categoryRedisTemplate.opsForValue().multiSet(categoryMap);

                // TTL 일괄 설정
                for (String key : categoryMap.keySet()) {
                    categoryRedisTemplate.expire(key, Duration.ofHours(TTL_HOURS));
                }

                log.debug("개별 카테고리 일괄 저장 완료: {} 개", categoryMap.size());
            }

        } catch (Exception e) {
            log.error("개별 카테고리 일괄 저장 실패", e);
            // MultiSet 실패 시 fallback으로 개별 저장
            fallbackSaveCategories(allCategories);
        }
    }

    /**
     * MultiSet 실패 시 fallback 개별 저장
     */
    private void fallbackSaveCategories(List<Category> allCategories) {
        log.warn("MultiSet 저장 실패, 개별 저장으로 fallback");
        for (Category category : allCategories) {
            try {
                CategoryDto dto = CategoryDto.from(category);
                addCategory(dto);
            } catch (Exception e) {
                log.error("Fallback 개별 저장 실패: id={}", category.getId(), e);
            }
        }
    }

    /**
     * 메모리에서 트리 구조 구성
     */
    private List<CategoryDto> buildCategoryTree(List<Category> allCategories) {
        // 부모-자식 관계 맵 생성
        Map<Long, List<Category>> parentChildMap = allCategories.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        // 루트 카테고리들 찾기
        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParent() == null)
                .sorted(Comparator.comparing(Category::getDisplayOrder))
                .toList();

        // 각 루트에 대해 트리 구성
        return rootCategories.stream()
                .map(root -> buildTreeRecursively(root, parentChildMap))
                .collect(Collectors.toList());
    }

    /**
     * 재귀적으로 트리 구성 (메모리에서만 처리)
     */
    private CategoryDto buildTreeRecursively(Category parent, Map<Long, List<Category>> parentChildMap) {
        CategoryDto parentDto = CategoryDto.from(parent);

        List<Category> children = parentChildMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            List<CategoryDto> childDtos = children.stream()
                    .sorted(Comparator.comparing(Category::getDisplayOrder))
                    .map(child -> buildTreeRecursively(child, parentChildMap))
                    .collect(Collectors.toList());
            parentDto.setChildren(childDtos);
        }

        return parentDto;
    }

    /**
     * 카테고리 수정 시 양쪽 캐시 업데이트
     */
    public void updateCategory(CategoryDto category, Long oldParentId) {
        // 1. 개별 캐시 업데이트
        addCategory(category);

        // 2. 트리 캐시는 무효화 (다음 조회 시 재구성)
        invalidateCategoryTree();

        log.debug("카테고리 업데이트: id={}, 트리 캐시 무효화", category.getId());
    }

    /**
     * 카테고리 삭제 시 양쪽 캐시에서 제거
     */
    public void removeCategory(CategoryDto category) {
        // 1. 개별 캐시에서 제거
        String key = CATEGORY_KEY_PREFIX + category.getId();
        try {
            categoryRedisTemplate.delete(key);
            log.debug("개별 카테고리 삭제: id={}", category.getId());
        } catch (Exception e) {
            log.error("개별 카테고리 삭제 실패: id={}", category.getId(), e);
        }

        // 2. 트리 캐시 무효화
        invalidateCategoryTree();
    }

    /**
     * 트리 캐시 무효화
     */
    public void invalidateCategoryTree() {
        try {
            treeRedisTemplate.delete(CATEGORY_TREE_KEY);
            log.debug("트리 캐시 무효화");
        } catch (Exception e) {
            log.error("트리 캐시 무효화 실패", e);
        }
    }

    /**
     * 모든 캐시 초기화
     */
    public void clearAllCache() {
        try {
            // 1. 개별 카테고리 키들 삭제
            Set<String> categoryKeys = categoryRedisTemplate.keys(CATEGORY_KEY_PREFIX + "*");
            if (categoryKeys != null && !categoryKeys.isEmpty()) {
                categoryRedisTemplate.delete(categoryKeys);
            }

            // 2. 트리 캐시 삭제
            treeRedisTemplate.delete(CATEGORY_TREE_KEY);

            log.info("모든 카테고리 캐시 삭제 완료");

        } catch (Exception e) {
            log.error("전체 캐시 삭제 실패", e);
        }
    }

    /**
     * 캐시 상태 확인
     */
    public boolean isCacheEmpty() {
        try {
            List<CategoryDto> tree = getCategoryTree();
            return tree.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * CategoryDto -> CategoryResponse 변환
     */
    private CategoryResponse convertToCategoryResponse(CategoryDto dto) {
        CategoryResponse response = CategoryResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .code(dto.getCode())
                .storeCode(dto.getStoreCode())
                .storeTitle(dto.getStoreTitle())
                .groupTitle(dto.getGroupTitle())
                .displayOrder(dto.getDisplayOrder())
                .genderFilter(dto.getGenderFilter())
                .parentId(dto.getParentId())
                .parentName(dto.getParentName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .isRoot(dto.getParentId() == null)
                .leaf(dto.getChildren() == null || dto.getChildren().isEmpty())
                .build();

        if (dto.getChildren() != null) {
            List<CategoryResponse> childResponses = dto.getChildren().stream()
                    .map(this::convertToCategoryResponse)
                    .collect(Collectors.toList());
            response.setChildren(childResponses);
        }

        return response;
    }

    /**
     * CategoryResponse -> CategoryDto 변환
     */
    private CategoryDto convertToCategoryDto(CategoryResponse response) {
        CategoryDto dto = CategoryDto.builder()
                .id(response.getId())
                .name(response.getName())
                .description(response.getDescription())
                .code(response.getCode())
                .storeCode(response.getStoreCode())
                .storeTitle(response.getStoreTitle())
                .groupTitle(response.getGroupTitle())
                .displayOrder(response.getDisplayOrder())
                .genderFilter(response.getGenderFilter())
                .parentId(response.getParentId())
                .parentName(response.getParentName())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();

        if (response.getChildren() != null) {
            List<CategoryDto> childDtos = response.getChildren().stream()
                    .map(this::convertToCategoryDto)
                    .collect(Collectors.toList());
            dto.setChildren(childDtos);
        }

        return dto;
    }

    // 기존 메서드들 호환성을 위해 유지
    public List<CategoryDto> getChildCategories(Long parentId) {
        // 전체 트리에서 해당 부모의 자식들 찾기
        List<CategoryDto> tree = getCategoryTree();
        CategoryDto parent = findCategoryInTree(tree, parentId);

        if (parent != null && parent.getChildren() != null) {
            return parent.getChildren();
        }

        return Collections.emptyList();
    }

    private CategoryDto findCategoryInTree(List<CategoryDto> tree, Long targetId) {
        for (CategoryDto category : tree) {
            if (category.getId().equals(targetId)) {
                return category;
            }

            if (category.getChildren() != null) {
                CategoryDto found = findCategoryInTree(category.getChildren(), targetId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public List<CategoryDto> getAllActiveCategories() {
        return getCategoryTree();
    }
}
