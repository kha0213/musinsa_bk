package com.yl.musinsa2.service;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryStatistics;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * 모든 활성화된 카테고리 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.debug("모든 카테고리 조회 시작");
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        log.debug("조회된 카테고리 수: {}", categories.size());
        
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 루트 카테고리들만 조회 (부모가 없는 카테고리들)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        log.debug("루트 카테고리 조회 시작");
        List<Category> rootCategories = categoryRepository.findByParentIsNullAndIsActiveTrue();
        log.debug("조회된 루트 카테고리 수: {}", rootCategories.size());
        
        return rootCategories.stream()
                .map(CategoryResponse::fromWithChildren)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 카테고리 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.debug("카테고리 조회 시작, ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
        
        log.debug("카테고리 조회 완료: {}", category.getName());
        return CategoryResponse.fromWithChildren(category);
    }
    
    /**
     * 특정 부모 카테고리의 자식 카테고리들 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getChildCategories(Long parentId) {
        log.debug("자식 카테고리 조회 시작, 부모 ID: {}", parentId);
        List<Category> childCategories = categoryRepository.findByParentIdAndIsActiveTrue(parentId);
        log.debug("조회된 자식 카테고리 수: {}", childCategories.size());
        
        return childCategories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 카테고리 생성
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
        log.info("카테고리 생성 완료: {}", savedCategory.getName());
        
        return CategoryResponse.from(savedCategory);
    }
    
    /**
     * 카테고리 수정
     */
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        log.debug("카테고리 수정 시작, ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("카테고리 수정 완료: {}", updatedCategory.getName());
        
        return CategoryResponse.from(updatedCategory);
    }
    
    /**
     * 카테고리 삭제 (논리적 삭제)
     */
    public void deleteCategory(Long id) {
        log.debug("카테고리 삭제 시작, ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
        
        // 자식 카테고리가 있는 경우 삭제 불가
        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");
        }
        
        category.setIsActive(false);
        categoryRepository.save(category);
        log.info("카테고리 삭제 완료: {}", category.getName());
    }
    
    /**
     * 카테고리 물리적 삭제 (실제 데이터베이스에서 제거)
     */
    public void permanentDeleteCategory(Long id) {
        log.debug("카테고리 영구 삭제 시작, ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
        
        // 자식 카테고리가 있는 경우 삭제 불가
        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");
        }
        
        categoryRepository.delete(category);
        log.warn("카테고리 영구 삭제 완료: {}", category.getName());
    }
    
    /**
     * 카테고리 이름으로 검색
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoriesByName(String name) {
        log.debug("카테고리 검색 시작, 키워드: {}", name);
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
        log.debug("검색 결과 수: {}", categories.size());
        
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 카테고리 활성화/비활성화
     */
    public CategoryResponse toggleCategoryStatus(Long id) {
        log.debug("카테고리 상태 토글 시작, ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
        
        boolean previousStatus = category.getIsActive();
        category.setIsActive(!previousStatus);
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("카테고리 상태 변경: {} -> {}", previousStatus, updatedCategory.getIsActive());
        
        return CategoryResponse.from(updatedCategory);
    }
    
    /**
     * 전체 카테고리 트리 구조 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        log.debug("카테고리 트리 조회 시작");
        return getRootCategories(); // 루트부터 시작하여 자식들까지 모두 포함
    }
    
    /**
     * 카테고리 통계 정보 조회
     */
    @Transactional(readOnly = true)
    public CategoryStatistics getCategoryStatistics() {
        log.debug("카테고리 통계 조회 시작");
        
        long totalCount = categoryRepository.countByIsActiveTrue();
        long rootCount = categoryRepository.findByParentIsNullAndIsActiveTrue().size();
        
        log.debug("카테고리 통계 - 전체: {}, 루트: {}, 하위: {}", totalCount, rootCount, totalCount - rootCount);
        
        return new CategoryStatistics(totalCount, rootCount);
    }
}
