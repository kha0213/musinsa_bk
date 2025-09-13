package com.yl.musinsa2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto implements Serializable {
    
    private Long id;
    private String name;
    private String description;
    private String code;
    private String storeCode;
    private String storeTitle;
    private String groupTitle;
    private Integer displayOrder;
    private GenderFilter genderFilter;
    private Long parentId;
    private String parentName;
    private List<CategoryDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private Integer sortOrder;
    
    public static CategoryDto from(Category category) {
        CategoryDtoBuilder builder = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .code(category.getCode())
                .storeCode(category.getStoreCode())
                .storeTitle(category.getStoreTitle())
                .groupTitle(category.getGroupTitle())
                .displayOrder(category.getDisplayOrder())
                .genderFilter(category.getGenderFilter())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .isActive(category.getIsActive())
                .sortOrder(category.getDisplayOrder() != null ? category.getDisplayOrder() : 0);
        
        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId())
                   .parentName(category.getParent().getName());
        }
        
        return builder.build();
    }
    
    public static CategoryDto fromWithChildren(Category category) {
        CategoryDto dto = from(category);
        
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryDto> children = category.getChildren().stream()
                    .filter(Category::getIsActive)
                    .map(CategoryDto::fromWithChildren)
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        
        return dto;
    }
}
