package com.yl.musinsa2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequest {
    
    @NotBlank(message = "카테고리 이름은 필수입니다")
    @Size(max = 100, message = "카테고리 이름은 100자를 초과할 수 없습니다")
    private String name;
    
    @Size(max = 500, message = "카테고리 설명은 500자를 초과할 수 없습니다")
    private String description;
    
    private Long parentId;
}
