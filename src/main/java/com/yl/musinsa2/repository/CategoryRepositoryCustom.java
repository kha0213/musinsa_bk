package com.yl.musinsa2.repository;

import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;

import java.util.List;

public interface CategoryRepositoryCustom {
    
    /**
     * 성별 필터가 전체이거나 특정 성별인 카테고리 조회
     */
    List<Category> findAllByGenderFilterIncludingAll(GenderFilter genderFilter);
    
    /**
     * 특정 카테고리의 모든 하위 카테고리를 재귀적으로 조회
     */
    List<Category> findCategoryTreeById(Long categoryId);
}
