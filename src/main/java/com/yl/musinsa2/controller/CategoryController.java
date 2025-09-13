package com.yl.musinsa2.controller;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "카테고리 관리", description = "온라인 쇼핑몰 카테고리 CRUD 및 관리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "카테고리 트리 구조 조회",
            description = "카테고리를 계층적 트리 구조로 조회합니다. 루트 카테고리부터 시작하여 모든 하위 카테고리를 포함합니다."
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        log.info("카테고리 트리 조회 요청");
        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
        log.info("카테고리 트리 조회 완료");

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)
                        .cachePublic()
                        .mustRevalidate())
                .body(categoryTree);
    }

    @Operation(
            summary = "특정 카테고리 조회",
            description = "ID로 특정 카테고리를 조회합니다. 해당 카테고리의 직속 하위 카테고리들도 함께 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id) {
        log.info("카테고리 조회 요청, ID: {}", id);
        CategoryResponse category = categoryService.getCategoryById(id);
        log.info("카테고리 조회 완료: {}", category.getName());
        return category;
    }

    @Operation(
            summary = "새 카테고리 생성",
            description = "새로운 카테고리를 생성합니다. parentId를 지정하면 해당 카테고리의 하위 카테고리로 생성됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "카테고리 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "생성 성공 응답",
                                    value = """
                                            {
                                              "id": 32,
                                              "name": "새 카테고리",
                                              "description": "새로운 카테고리입니다",
                                              "parentId": 1,
                                              "parentName": "남성",
                                              "children": null,
                                              "createdAt": "2024-01-15T10:30:00",
                                              "updatedAt": "2024-01-15T10:30:00",
                                              "root": false,
                                              "leaf": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", description = "부모 카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 카테고리 정보",
                    required = true,
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "루트 카테고리 생성",
                                            description = "최상위 카테고리 생성 예시",
                                            value = """
                                                    {
                                                      "name": "스포츠",
                                                      "description": "스포츠 용품 및 의류",
                                                      "parentId": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "하위 카테고리 생성",
                                            description = "기존 카테고리의 하위 카테고리 생성 예시",
                                            value = """
                                                    {
                                                      "name": "운동화",
                                                      "description": "각종 운동화",
                                                      "parentId": 4
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody CategoryCreateRequest request) {
        log.info("카테고리 생성 요청: {}", request.getName());
        CategoryResponse createdCategory = categoryService.createCategory(request);
        log.info("카테고리 생성 완료: {}", createdCategory.getName());
        return createdCategory;
    }

    @Operation(
            summary = "카테고리 정보 수정",
            description = "기존 카테고리의 이름, 설명을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @Parameter(description = "수정할 카테고리 ID", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 카테고리 정보",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "카테고리 수정 예시",
                                    value = """
                                            {
                                              "name": "수정된 카테고리명",
                                              "description": "수정된 카테고리 설명"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody CategoryUpdateRequest request) {
        log.info("카테고리 수정 요청, ID: {}", id);
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
        log.info("카테고리 수정 완료: {}", updatedCategory.getName());
        return updatedCategory;
    }

    @Operation(
            summary = "카테고리 삭제 (Soft Delete)",
            description = "카테고리를 소프트 삭제합니다. Hibernate의 @SoftDelete 어노테이션이 자동으로 deleted=true로 설정합니다. 하위 카테고리가 있는 경우 삭제할 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "하위 카테고리가 존재하여 삭제 불가"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @Parameter(description = "삭제할 카테고리 ID", example = "1")
            @PathVariable Long id) {
        log.info("카테고리 삭제 요청, ID: {}", id);
        categoryService.deleteCategory(id);
        log.info("카테고리 삭제 완료, ID: {}", id);
    }

    @Operation(
            summary = "전체 카테고리 검색",
            description = "모든 카테고리를 트리 구조로 반환합니다. 검색어가 있으면 해당하는 카테고리만 필터링하여 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "전체 트리 검색 결과",
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "name": "남성",
                                                "description": "남성 의류",
                                                "children": [
                                                  {
                                                    "id": 6,
                                                    "name": "상의",
                                                    "description": "남성 상의",
                                                    "children": []
                                                  }
                                                ]
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/search")
    public List<CategoryResponse> searchCategoriesTree(
            @Parameter(description = "검색할 카테고리 이름 (선택사항)", example = "티셔츠")
            @RequestParam(required = false) String name,
            HttpServletResponse response) {
        log.info("전체 카테고리 트리 검색 요청, 키워드: {}", name);

        // HTTP 캐시 헤더 설정
        response.setHeader("Cache-Control", "max-age=1800, public, must-revalidate");
        response.setHeader("Expires", String.valueOf(System.currentTimeMillis() + 1800000)); // 30분

        List<CategoryResponse> categories = categoryService.searchCategoriesTree(name);
        log.info("전체 카테고리 트리 검색 완료, 결과 수: {}", categories.size());

        return categories;
    }

    @Operation(
            summary = "특정 카테고리 하위 검색",
            description = "특정 카테고리 ID를 기준으로 해당 카테고리와 그 하위 카테고리들을 트리 구조로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "하위 카테고리 트리 검색 결과",
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "남성",
                                              "description": "남성 의류",
                                              "children": [
                                                {
                                                  "id": 6,
                                                  "name": "상의",
                                                  "description": "남성 상의",
                                                  "children": [
                                                    {
                                                      "id": 11,
                                                      "name": "티셔츠",
                                                      "children": []
                                                    }
                                                  ]
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @GetMapping("/search/{categoryId}")
    public ResponseEntity<CategoryResponse> searchCategorySubTree(
            @Parameter(description = "기준 카테고리 ID", example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "검색할 카테고리 이름 (선택사항)", example = "티셔츠")
            @RequestParam(required = false) String name) {
        log.info("특정 카테고리 하위 트리 검색 요청, 카테고리 ID: {}, 키워드: {}", categoryId, name);
        CategoryResponse category = categoryService.searchCategorySubTree(categoryId, name);
        log.info("특정 카테고리 하위 트리 검색 완료: {}", category.getName());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)
                        .cachePublic()
                        .mustRevalidate())
                .body(category);
    }

    @Operation(
            summary = "카테고리 캐시 수동 갱신",
            description = "카테고리 캐시를 수동으로 갱신합니다. DB와 캐시 데이터 불일치 발생 시 사용할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "캐시 갱신 성공")
    })
    @PostMapping("/cache/refresh")
    public void refreshCategoryCache() {
        log.info("카테고리 캐시 수동 갱신 요청");
        categoryService.refreshCache();
        log.info("카테고리 캐시 수동 갱신 완료");
    }
}
