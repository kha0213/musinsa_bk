package com.yl.musinsa2.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import com.yl.musinsa2.entity.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCategory category = QCategory.category;

    @Override
    public List<Category> findAllByGenderFilterIncludingAll(GenderFilter genderFilter) {
        return queryFactory
                .selectFrom(category)
                .where(
                    category.genderFilter.eq(GenderFilter.ALL)
                    .or(category.genderFilter.eq(genderFilter))
                )
                .orderBy(category.displayOrder.asc())
                .fetch();
    }

    @Override
    public List<Category> findCategoryTreeById(Long categoryId) {
        // QueryDSL로는 재귀 쿼리가 직접적으로 지원되지 않으므로
        // Native Query를 사용하거나 Java에서 재귀 로직을 구현해야 합니다.
        // 여기서는 Java에서 재귀적으로 처리하는 방식을 사용합니다.
        
        // 먼저 루트 카테고리를 가져옵니다
        Category root = queryFactory
                .selectFrom(category)
                .where(category.id.eq(categoryId))
                .fetchOne();
        
        if (root == null) {
            return List.of();
        }
        
        // 재귀적으로 모든 하위 카테고리를 가져옵니다
        return findAllDescendants(categoryId);
    }
    
    private List<Category> findAllDescendants(Long parentId) {
        // 직접적인 자식들을 가져옵니다
        List<Category> directChildren = queryFactory
                .selectFrom(category)
                .where(category.parent.id.eq(parentId))
                .orderBy(category.displayOrder.asc(), category.name.asc())
                .fetch();
        
        // 결과 리스트 초기화
        List<Category> allDescendants = new java.util.ArrayList<>(directChildren);
        
        // 각 직접 자식에 대해 재귀적으로 하위 카테고리들을 가져옵니다
        for (Category child : directChildren) {
            allDescendants.addAll(findAllDescendants(child.getId()));
        }
        
        return allDescendants;
    }
}
