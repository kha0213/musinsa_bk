package com.yl.musinsa2.controller;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 관리")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 트리 구조 조회")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)
                        .cachePublic()
                        .mustRevalidate())
                .body(categoryTree);
    }

    @Operation(summary = "특정 카테고리 조회")
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @Operation(summary = "새 카테고리 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    @Operation(summary = "카테고리 정보 수정")
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, 
                                         @Valid @RequestBody CategoryUpdateRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @Operation(summary = "전체 카테고리 검색")
    @GetMapping("/search")
    public List<CategoryResponse> searchCategoriesTree(@RequestParam(required = false) String name,
                                                       HttpServletResponse response) {
        // HTTP 캐시 설정
        response.setHeader("Cache-Control", "max-age=1800, public, must-revalidate");
        response.setHeader("Expires", String.valueOf(System.currentTimeMillis() + 1800000));

        return categoryService.searchCategoriesTree(name);
    }

    @Operation(summary = "특정 카테고리 하위 검색")
    @GetMapping("/search/{categoryId}")
    public ResponseEntity<CategoryResponse> searchCategorySubTree(@PathVariable Long categoryId,
                                                                  @RequestParam(required = false) String name) {
        CategoryResponse category = categoryService.searchCategorySubTree(categoryId, name);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)
                        .cachePublic()
                        .mustRevalidate())
                .body(category);
    }

    @Operation(summary = "카테고리 캐시 수동 갱신")
    @PostMapping("/cache/refresh")
    public void refreshCategoryCache() {
        categoryService.refreshCache();
    }
}
