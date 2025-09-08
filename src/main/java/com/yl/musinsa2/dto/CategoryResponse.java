package com.yl.musinsa2.dto;

import com.yl.musinsa2.entity.Category;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private boolean isRoot;
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
