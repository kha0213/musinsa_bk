package com.yl.musinsa2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "카테고리 통계 정보")
public class CategoryStatistics {
    
    @Schema(description = "전체 카테고리 수", example = "31")
    private final long totalCategories;
    
    @Schema(description = "루트 카테고리 수", example = "5")
    private final long rootCategories;

    @Schema(description = "하위 카테고리 수", example = "26")
    public long getSubCategories() {
        return totalCategories - rootCategories;
    }
}
