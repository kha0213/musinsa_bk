package com.yl.musinsa2.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.QCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QueryDSL Q클래스 생성 및 동작 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QueryDslGenerationTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    void testQCategoryGeneration() {
        // Given: Q클래스가 정상적으로 생성되었는지 확인
        QCategory qCategory = QCategory.category;
        
        // Then: Q클래스의 필드들이 정상적으로 접근 가능한지 확인
        assertThat(qCategory.id).isNotNull();
        assertThat(qCategory.name).isNotNull();
        assertThat(qCategory.description).isNotNull();
        assertThat(qCategory.isActive).isNotNull();
        assertThat(qCategory.genderFilter).isNotNull();
        assertThat(qCategory.displayOrder).isNotNull();
        assertThat(qCategory.code).isNotNull();
        assertThat(qCategory.parent).isNotNull();
        assertThat(qCategory.children).isNotNull();
        
        System.out.println("✅ QCategory 클래스가 정상적으로 생성되었습니다!");
        System.out.println("✅ 모든 필드에 접근 가능합니다!");
    }

    @Test
    void testQueryDslQuery() {
        // Given: QueryDSL로 간단한 쿼리 실행
        QCategory qCategory = QCategory.category;
        
        // When: 활성 카테고리 개수 조회
        Long count = queryFactory
                .select(qCategory.count())
                .from(qCategory)
                .where(qCategory.isActive.eq(true))
                .fetchOne();
        
        // Then: 쿼리가 정상 실행되는지 확인
        assertThat(count).isNotNull();
        assertThat(count).isGreaterThanOrEqualTo(0);
        
        System.out.println("✅ QueryDSL 쿼리가 정상 실행되었습니다!");
        System.out.println("활성 카테고리 개수: " + count);
    }

    @Test
    void testComplexQuery() {
        // Given: 복잡한 QueryDSL 쿼리 테스트
        QCategory qCategory = QCategory.category;
        
        // When: 루트 카테고리들 조회
        var rootCategories = queryFactory
                .selectFrom(qCategory)
                .where(qCategory.parent.isNull()
                        .and(qCategory.isActive.eq(true)))
                .orderBy(qCategory.displayOrder.asc())
                .fetch();
        
        // Then: 쿼리 결과 확인
        assertThat(rootCategories).isNotNull();
        
        System.out.println("✅ 복잡한 QueryDSL 쿼리도 정상 작동합니다!");
        System.out.println("루트 카테고리 개수: " + rootCategories.size());
    }
}
