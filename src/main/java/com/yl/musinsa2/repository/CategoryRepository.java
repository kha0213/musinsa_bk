package com.yl.musinsa2.repository;

import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    // 루트 카테고리(부모가 없는 카테고리) 조회
    List<Category> findByParentIsNull();

    // 특정 부모의 자식 카테고리들 조회
    List<Category> findByParentId(Long parentId);

    // 이름으로 카테고리 검색
    List<Category> findByNameContainingIgnoreCase(String name);

    // 특정 부모 카테고리의 직접적인 자식들만 조회
    List<Category> findByParent(Category parent);

    // 이름으로 카테고리 조회 (대소문자 구분 없음)
    Optional<Category> findByNameIgnoreCase(String name);

    // 특정 부모의 자식 카테고리 개수 조회
    long countByParentId(Long parentId);

    //  성별 필터와 표시 순서로 조회
    List<Category> findAllByGenderFilterOrderByDisplayOrder(GenderFilter genderFilter);

    // 코드로 카테고리 조회
    Optional<Category> findByCode(String code);

    // 표시 순서로 카테고리 조회
    List<Category> findByOrderByDisplayOrder();
}
