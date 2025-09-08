package com.yl.musinsa2.controller;

import com.yl.musinsa2.dto.CategoryCreateRequest;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.dto.CategoryStatistics;
import com.yl.musinsa2.dto.CategoryUpdateRequest;
import com.yl.musinsa2.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*") // 개발용
@RequiredArgsConstructor
@Tag(name = "카테고리 관리", description = "온라인 쇼핑몰 카테고리 CRUD 및 관리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "전체 카테고리 조회",
            description = "시스템에 등록된 모든 활성화된 카테고리를 조회합니다. 평면적인 리스트 형태로 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "name": "남성",
                                                "description": "남성 의류 및 잡화",
                                                "parentId": null,
                                                "parentName": null,
                                                "children": null,
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-15T10:30:00",
                                                "isActive": true,
                                                "root": true,
                                                "leaf": false
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        log.info("모든 카테고리 조회 요청");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        log.info("카테고리 {} 개 조회 완료", categories.size());
        return categories;
    }

    @Operation(
            summary = "카테고리 트리 구조 조회",
            description = "카테고리를 계층적 트리 구조로 조회합니다. 루트 카테고리부터 시작하여 모든 하위 카테고리를 포함합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "트리 구조 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "트리 구조 응답 예시",
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "name": "남성",
                                                "description": "남성 의류 및 잡화",
                                                "parentId": null,
                                                "parentName": null,
                                                "children": [
                                                  {
                                                    "id": 6,
                                                    "name": "상의",
                                                    "description": "남성 상의",
                                                    "parentId": 1,
                                                    "parentName": "남성",
                                                    "children": null,
                                                    "isActive": true
                                                  }
                                                ],
                                                "isActive": true,
                                                "root": true,
                                                "leaf": false
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/tree")
    public List<CategoryResponse> getCategoryTree() {
        log.info("카테고리 트리 조회 요청");
        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
        log.info("카테고리 트리 조회 완료");
        return categoryTree;
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
            summary = "자식 카테고리 목록 조회",
            description = "특정 부모 카테고리의 직속 자식 카테고리들을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자식 카테고리 조회 성공")
    })
    @GetMapping("/{parentId}/children")
    public List<CategoryResponse> getChildCategories(
            @Parameter(description = "부모 카테고리 ID", example = "1")
            @PathVariable Long parentId) {
        log.info("자식 카테고리 조회 요청, 부모 ID: {}", parentId);
        List<CategoryResponse> children = categoryService.getChildCategories(parentId);
        log.info("자식 카테고리 {} 개 조회 완료", children.size());
        return children;
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
                                              "isActive": true,
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
            description = "기존 카테고리의 이름, 설명, 활성화 상태를 수정합니다."
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
                                              "description": "수정된 카테고리 설명",
                                              "isActive": true
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
            summary = "카테고리 삭제 (논리적)",
            description = "카테고리를 논리적으로 삭제합니다(비활성화). 하위 카테고리가 있는 경우 삭제할 수 없습니다."
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
            summary = "카테고리 영구 삭제",
            description = "카테고리를 데이터베이스에서 완전히 삭제합니다. 하위 카테고리가 있는 경우 삭제할 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "영구 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "하위 카테고리가 존재하여 삭제 불가"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @DeleteMapping("/{id}/permanent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void permanentDeleteCategory(
            @Parameter(description = "영구 삭제할 카테고리 ID", example = "1")
            @PathVariable Long id) {
        log.info("카테고리 영구 삭제 요청, ID: {}", id);
        categoryService.permanentDeleteCategory(id);
        log.info("카테고리 영구 삭제 완료, ID: {}", id);
    }

    @Operation(
            summary = "카테고리 검색",
            description = "카테고리 이름으로 검색합니다. 대소문자를 구분하지 않으며 부분 일치 검색을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public List<CategoryResponse> searchCategories(
            @Parameter(description = "검색할 카테고리 이름", example = "티셔츠")
            @RequestParam String name) {
        log.info("카테고리 검색 요청, 키워드: {}", name);
        List<CategoryResponse> categories = categoryService.searchCategoriesByName(name);
        log.info("카테고리 검색 완료, 결과 수: {}", categories.size());
        return categories;
    }

    @Operation(
            summary = "카테고리 활성화 상태 토글",
            description = "카테고리의 활성화 상태를 토글합니다. 활성화된 카테고리는 비활성화되고, 비활성화된 카테고리는 활성화됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음", ref = "#/components/responses/NotFound")
    })
    @PatchMapping("/{id}/toggle-status")
    public CategoryResponse toggleCategoryStatus(
            @Parameter(description = "상태를 변경할 카테고리 ID", example = "1")
            @PathVariable Long id) {
        log.info("카테고리 상태 토글 요청, ID: {}", id);
        CategoryResponse updatedCategory = categoryService.toggleCategoryStatus(id);
        log.info("카테고리 상태 변경 완료: {} -> {}", id, updatedCategory.getIsActive());
        return updatedCategory;
    }

    @Operation(
            summary = "카테고리 통계 조회",
            description = "전체 카테고리 수, 루트 카테고리 수, 하위 카테고리 수 등의 통계 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "통계 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "통계 응답 예시",
                                    value = """
                                            {
                                              "totalCategories": 31,
                                              "rootCategories": 5,
                                              "subCategories": 26
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/statistics")
    public CategoryStatistics getCategoryStatistics() {
        log.info("카테고리 통계 조회 요청");
        CategoryStatistics statistics = categoryService.getCategoryStatistics();
        log.info("카테고리 통계 조회 완료 - 전체: {}, 루트: {}",
                statistics.getTotalCategories(), statistics.getRootCategories());
        return statistics;
    }
}
