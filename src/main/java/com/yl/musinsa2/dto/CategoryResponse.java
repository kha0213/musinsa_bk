package com.yl.musinsa2.dto;

import com.yl.musinsa2.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 응답 DTO")
public class CategoryResponse {
    
    @Schema(description = "카테고리 ID", example = "1")
    private Long id;
    
    @Schema(description = "카테고리 이름", example = "남성")
    private String name;
    
    @Schema(description = "카테고리 설명", example = "남성 의류 및 잡화")
    private String description;
    
    @Schema(description = "부모 카테고리 ID", example = "null")
    private Long parentId;
    
    @Schema(description = "부모 카테고리 이름", example = "null")
    private String parentName;
    
    @Schema(description = "하위 카테고리 목록")
    private List<CategoryResponse> children;
    
    @Schema(description = "생성 일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "활성화 상태", example = "true")
    private Boolean isActive;
    
    @Schema(description = "루트 카테고리 여부", example = "true")
    private boolean isRoot;
    
    @Schema(description = "리프 카테고리 여부 (자식이 없는지)", example = "false")
    private boolean isLeaf;
    
    // Entity에서 DTO로 변환하는 생성자
    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
        this.isActive = category.getIsActive();
        this.isRoot = category.isRoot();
        this.isLeaf = category.isLeaf();
        
        if (category.getParent() != null) {
            this.parentId = category.getParent().getId();
            this.parentName = category.getParent().getName();
        }
    }
    
    // 자식 카테고리까지 포함하는 생성자
    public CategoryResponse(Category category, boolean includeChildren) {
        this(category);
        
        if (includeChildren && !category.getChildren().isEmpty()) {
            this.children = category.getChildren().stream()
                    .map(child -> new CategoryResponse(child, false)) // 무한 재귀 방지
                    .collect(Collectors.toList());
        }
    }
    
    // Static factory methods
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category);
    }
    
    public static CategoryResponse fromWithChildren(Category category) {
        return new CategoryResponse(category, true);
    }
}
