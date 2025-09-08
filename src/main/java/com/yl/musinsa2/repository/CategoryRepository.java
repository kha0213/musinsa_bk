package com.yl.musinsa2.repository;

import com.yl.musinsa2.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 루트 카테고리(부모가 없는 카테고리) 조회
    List<Category> findByParentIsNullAndIsActiveTrue();
    
    // 특정 부모의 자식 카테고리들 조회
    List<Category> findByParentIdAndIsActiveTrue(Long parentId);
    
    // 활성화된 모든 카테고리 조회
    List<Category> findByIsActiveTrue();
    
    // 이름으로 카테고리 검색
    List<Category> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    // 특정 카테고리의 모든 하위 카테고리를 재귀적으로 조회
    @Query(value = """
        WITH RECURSIVE category_tree AS (
            SELECT id, name, description, parent_id, created_at, updated_at, is_active, 0 as level
            FROM categories 
            WHERE id = :categoryId AND is_active = true
            
            UNION ALL
            
            SELECT c.id, c.name, c.description, c.parent_id, c.created_at, c.updated_at, c.is_active, ct.level + 1
            FROM categories c
            INNER JOIN category_tree ct ON c.parent_id = ct.id
            WHERE c.is_active = true
        )
        SELECT * FROM category_tree ORDER BY level, name
        """, nativeQuery = true)
    List<Category> findCategoryTreeById(@Param("categoryId") Long categoryId);
    
    // 특정 부모 카테고리의 직접적인 자식들만 조회
    List<Category> findByParentAndIsActiveTrue(Category parent);
    
    // 이름으로 카테고리 조회 (대소문자 구분 없음)
    Optional<Category> findByNameIgnoreCaseAndIsActiveTrue(String name);
    
    // 카테고리 개수 조회
    long countByIsActiveTrue();
    
    // 특정 부모의 자식 카테고리 개수 조회
    long countByParentIdAndIsActiveTrue(Long parentId);
}
