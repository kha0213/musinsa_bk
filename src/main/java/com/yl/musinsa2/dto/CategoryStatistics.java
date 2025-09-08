package com.yl.musinsa2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryStatistics {
    private final long totalCategories;
    private final long rootCategories;

    public long getSubCategories() {
        return totalCategories - rootCategories;
    }
}