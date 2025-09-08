package com.yl.musinsa2.controller;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryStatistics;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*") // 개발용
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * 모든 카테고리 조회
     * GET /api/categories
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        log.info("모든 카테고리 조회 요청");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        log.info("카테고리 {} 개 조회 완료", categories.size());
        return categories;
    }
    
    /**
     * 루트 카테고리들 조회 (트리 구조)
     * GET /api/categories/tree
     */
    @GetMapping("/tree")
    public List<CategoryResponse> getCategoryTree() {
        log.info("카테고리 트리 조회 요청");
        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
        log.info("카테고리 트리 조회 완료");
        return categoryTree;
    }
    
    /**
     * 특정 카테고리 조회
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        log.info("카테고리 조회 요청, ID: {}", id);
        CategoryResponse category = categoryService.getCategoryById(id);
        log.info("카테고리 조회 완료: {}", category.getName());
        return category;
    }
    
    /**
     * 특정 부모 카테고리의 자식들 조회
     * GET /api/categories/{parentId}/children
     */
    @GetMapping("/{parentId}/children")
    public List<CategoryResponse> getChildCategories(@PathVariable Long parentId) {
        log.info("자식 카테고리 조회 요청, 부모 ID: {}", parentId);
        List<CategoryResponse> children = categoryService.getChildCategories(parentId);
        log.info("자식 카테고리 {} 개 조회 완료", children.size());
        return children;
    }
    
    /**
     * 카테고리 생성
     * POST /api/categories
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // 201 Created
    public CategoryResponse createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("카테고리 생성 요청: {}", request.getName());
        CategoryResponse createdCategory = categoryService.createCategory(request);
        log.info("카테고리 생성 완료: {}", createdCategory.getName());
        return createdCategory;
    }
    
    /**
     * 카테고리 수정
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request) {
        log.info("카테고리 수정 요청, ID: {}", id);
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
        log.info("카테고리 수정 완료: {}", updatedCategory.getName());
        return updatedCategory;
    }
    
    /**
     * 카테고리 삭제 (논리적 삭제)
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // 204 No Content
    public void deleteCategory(@PathVariable Long id) {
        log.info("카테고리 삭제 요청, ID: {}", id);
        categoryService.deleteCategory(id);
        log.info("카테고리 삭제 완료, ID: {}", id);
    }
    
    /**
     * 카테고리 물리적 삭제
     * DELETE /api/categories/{id}/permanent
     */
    @DeleteMapping("/{id}/permanent")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // 204 No Content
    public void permanentDeleteCategory(@PathVariable Long id) {
        log.info("카테고리 영구 삭제 요청, ID: {}", id);
        categoryService.permanentDeleteCategory(id);
        log.info("카테고리 영구 삭제 완료, ID: {}", id);
    }
    
    /**
     * 카테고리 검색
     * GET /api/categories/search?name={name}
     */
    @GetMapping("/search")
    public List<CategoryResponse> searchCategories(@RequestParam String name) {
        log.info("카테고리 검색 요청, 키워드: {}", name);
        List<CategoryResponse> categories = categoryService.searchCategoriesByName(name);
        log.info("카테고리 검색 완료, 결과 수: {}", categories.size());
        return categories;
    }
    
    /**
     * 카테고리 활성화/비활성화 토글
     * PATCH /api/categories/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public CategoryResponse toggleCategoryStatus(@PathVariable Long id) {
        log.info("카테고리 상태 토글 요청, ID: {}", id);
        CategoryResponse updatedCategory = categoryService.toggleCategoryStatus(id);
        log.info("카테고리 상태 변경 완료: {} -> {}", id, updatedCategory.getIsActive());
        return updatedCategory;
    }
    
    /**
     * 카테고리 통계 조회
     * GET /api/categories/statistics
     */
    @GetMapping("/statistics")
    public CategoryStatistics getCategoryStatistics() {
        log.info("카테고리 통계 조회 요청");
        CategoryStatistics statistics = categoryService.getCategoryStatistics();
        log.info("카테고리 통계 조회 완료 - 전체: {}, 루트: {}", 
                statistics.getTotalCategories(), statistics.getRootCategories());
        return statistics;
    }
}
