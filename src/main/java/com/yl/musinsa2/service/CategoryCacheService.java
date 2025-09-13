package com.yl.musinsa2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl.musinsa2.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String CATEGORY_KEY_PREFIX = "category:";
    private static final String CHILDREN_KEY_PREFIX = "children:";
    private static final String ALL_CATEGORIES_KEY = "categories:all";
    private static final String ROOT_CATEGORIES_KEY = "categories:root";
    private static final long TTL_SECONDS = 3600; // 1시간
    
    /**
     * 카테고리 추가
     */
    public void addCategory(CategoryDto category) {
        try {
            // 1. 개별 카테고리 저장
            String categoryKey = CATEGORY_KEY_PREFIX + category.getId();
            redisTemplate.opsForValue().set(categoryKey, category, Duration.ofSeconds(TTL_SECONDS));
            
            // 2. 전체 카테고리 목록에 추가
            redisTemplate.opsForSet().add(ALL_CATEGORIES_KEY, category.getId());
            redisTemplate.expire(ALL_CATEGORIES_KEY, Duration.ofSeconds(TTL_SECONDS));
            
            // 3. 부모-자식 관계 업데이트
            if (category.getParentId() != null) {
                String childrenKey = CHILDREN_KEY_PREFIX + category.getParentId();
                redisTemplate.opsForSet().add(childrenKey, category.getId());
                redisTemplate.expire(childrenKey, Duration.ofSeconds(TTL_SECONDS));
            } else {
                // 루트 카테고리인 경우
                redisTemplate.opsForSet().add(ROOT_CATEGORIES_KEY, category.getId());
                redisTemplate.expire(ROOT_CATEGORIES_KEY, Duration.ofSeconds(TTL_SECONDS));
            }
            
            log.debug("카테고리 캐시 추가 완료: {}", category.getId());
            
        } catch (Exception e) {
            log.error("카테고리 캐시 추가 실패: {}", category.getId(), e);
        }
    }
    
    /**
     * 카테고리 수정
     */
    public void updateCategory(CategoryDto category, Long oldParentId) {
        try {
            // 1. 개별 카테고리 업데이트
            String categoryKey = CATEGORY_KEY_PREFIX + category.getId();
            redisTemplate.opsForValue().set(categoryKey, category, Duration.ofSeconds(TTL_SECONDS));
            
            // 2. 부모가 변경된 경우 관계 업데이트
            if (!Objects.equals(oldParentId, category.getParentId())) {
                // 기존 부모에서 제거
                if (oldParentId != null) {
                    String oldChildrenKey = CHILDREN_KEY_PREFIX + oldParentId;
                    redisTemplate.opsForSet().remove(oldChildrenKey, category.getId());
                } else {
                    redisTemplate.opsForSet().remove(ROOT_CATEGORIES_KEY, category.getId());
                }
                
                // 새 부모에 추가
                if (category.getParentId() != null) {
                    String newChildrenKey = CHILDREN_KEY_PREFIX + category.getParentId();
                    redisTemplate.opsForSet().add(newChildrenKey, category.getId());
                    redisTemplate.expire(newChildrenKey, Duration.ofSeconds(TTL_SECONDS));
                } else {
                    redisTemplate.opsForSet().add(ROOT_CATEGORIES_KEY, category.getId());
                    redisTemplate.expire(ROOT_CATEGORIES_KEY, Duration.ofSeconds(TTL_SECONDS));
                }
            }
            
            log.debug("카테고리 캐시 수정 완료: {}", category.getId());
            
        } catch (Exception e) {
            log.error("카테고리 캐시 수정 실패: {}", category.getId(), e);
        }
    }
    
    /**
     * 카테고리 삭제
     */
    public void removeCategory(CategoryDto category) {
        try {
            // 1. 개별 카테고리 삭제
            String categoryKey = CATEGORY_KEY_PREFIX + category.getId();
            redisTemplate.delete(categoryKey);
            
            // 2. 전체 목록에서 제거
            redisTemplate.opsForSet().remove(ALL_CATEGORIES_KEY, category.getId());
            
            // 3. 부모-자식 관계에서 제거
            if (category.getParentId() != null) {
                String childrenKey = CHILDREN_KEY_PREFIX + category.getParentId();
                redisTemplate.opsForSet().remove(childrenKey, category.getId());
            } else {
                redisTemplate.opsForSet().remove(ROOT_CATEGORIES_KEY, category.getId());
            }
            
            // 4. 자식 관계 키 삭제
            String childrenKey = CHILDREN_KEY_PREFIX + category.getId();
            redisTemplate.delete(childrenKey);
            
            log.debug("카테고리 캐시 삭제 완료: {}", category.getId());
            
        } catch (Exception e) {
            log.error("카테고리 캐시 삭제 실패: {}", category.getId(), e);
        }
    }
    
    /**
     * 개별 카테고리 조회
     */
    public CategoryDto getCategory(Long id) {
        try {
            String categoryKey = CATEGORY_KEY_PREFIX + id;
            Object categoryData = redisTemplate.opsForValue().get(categoryKey);
            
            if (categoryData != null) {
                return objectMapper.convertValue(categoryData, CategoryDto.class);
            }
        } catch (Exception e) {
            log.error("카테고리 캐시 조회 실패: {}", id, e);
        }
        
        return null;
    }
    
    /**
     * 카테고리 트리 구조 조회
     */
    public List<CategoryDto> getCategoryTree() {
        try {
            Set<Object> rootIds = redisTemplate.opsForSet().members(ROOT_CATEGORIES_KEY);
            if (rootIds == null || rootIds.isEmpty()) {
                log.warn("루트 카테고리가 캐시에 없습니다. DB에서 초기화가 필요합니다.");
                return Collections.emptyList();
            }
            
            List<CategoryDto> rootCategories = new ArrayList<>();
            for (Object rootId : rootIds) {
                CategoryDto rootCategory = getCategory(Long.valueOf(rootId.toString()));
                if (rootCategory != null) {
                    buildCategoryTree(rootCategory);
                    rootCategories.add(rootCategory);
                }
            }
            
            return rootCategories.stream()
                    .sorted(Comparator.comparing(CategoryDto::getSortOrder))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("카테고리 트리 캐시 조회 실패", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 특정 부모의 자식 카테고리들 조회
     */
    public List<CategoryDto> getChildCategories(Long parentId) {
        try {
            String childrenKey = CHILDREN_KEY_PREFIX + parentId;
            Set<Object> childIds = redisTemplate.opsForSet().members(childrenKey);
            
            if (childIds == null || childIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<CategoryDto> children = new ArrayList<>();
            for (Object childId : childIds) {
                CategoryDto child = getCategory(Long.valueOf(childId.toString()));
                if (child != null && child.getIsActive()) {
                    children.add(child);
                }
            }
            
            return children.stream()
                    .sorted(Comparator.comparing(CategoryDto::getSortOrder))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("자식 카테고리 캐시 조회 실패, 부모 ID: {}", parentId, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 모든 활성 카테고리 조회
     */
    public List<CategoryDto> getAllActiveCategories() {
        try {
            Set<Object> allIds = redisTemplate.opsForSet().members(ALL_CATEGORIES_KEY);
            if (allIds == null || allIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<CategoryDto> categories = new ArrayList<>();
            for (Object id : allIds) {
                CategoryDto category = getCategory(Long.valueOf(id.toString()));
                if (category != null && category.getIsActive()) {
                    categories.add(category);
                }
            }
            
            return categories;
            
        } catch (Exception e) {
            log.error("전체 카테고리 캐시 조회 실패", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 캐시 전체 초기화
     */
    public void clearAllCache() {
        try {
            Set<String> keys = redisTemplate.keys("category:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            
            redisTemplate.delete(ALL_CATEGORIES_KEY);
            redisTemplate.delete(ROOT_CATEGORIES_KEY);
            
            // children 키들도 삭제
            Set<String> childrenKeys = redisTemplate.keys("children:*");
            if (childrenKeys != null && !childrenKeys.isEmpty()) {
                redisTemplate.delete(childrenKeys);
            }
            
            log.info("카테고리 캐시 전체 초기화 완료");
            
        } catch (Exception e) {
            log.error("카테고리 캐시 초기화 실패", e);
        }
    }
    
    /**
     * 재귀적으로 트리 구조 빌드
     */
    private void buildCategoryTree(CategoryDto parent) {
        String childrenKey = CHILDREN_KEY_PREFIX + parent.getId();
        Set<Object> childIds = redisTemplate.opsForSet().members(childrenKey);
        
        if (childIds != null && !childIds.isEmpty()) {
            List<CategoryDto> children = new ArrayList<>();
            for (Object childId : childIds) {
                CategoryDto child = getCategory(Long.valueOf(childId.toString()));
                if (child != null && child.getIsActive()) {
                    buildCategoryTree(child);
                    children.add(child);
                }
            }
            parent.setChildren(children.stream()
                    .sorted(Comparator.comparing(CategoryDto::getSortOrder))
                    .collect(Collectors.toList()));
        }
    }
    
    /**
     * 캐시 상태 확인
     */
    public boolean isCacheEmpty() {
        Set<Object> rootIds = redisTemplate.opsForSet().members(ROOT_CATEGORIES_KEY);
        return rootIds == null || rootIds.isEmpty();
    }
}
