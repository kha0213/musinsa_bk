package com.yl.musinsa2.service;

import com.yl.musinsa2.entity.GenderFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QueryDSL과 OpenFeign 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
class IntegratedTestServiceTest {

    @Autowired
    private IntegratedTestService integratedTestService;

    @Test
    void testQueryDslAndOpenFeignIntegration() {
        // Given
        GenderFilter genderFilter = GenderFilter.ALL;
        Long postId = 1L;

        // When
        String result = integratedTestService.getCategoryCountAndExternalPost(genderFilter, postId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("활성 카테고리 수:");
        assertThat(result).contains("외부 포스트 제목:");
        
        System.out.println("통합 테스트 결과: " + result);
    }

    @Test
    void testWithDifferentGenderFilter() {
        // Given
        GenderFilter genderFilter = GenderFilter.MALE;
        Long postId = 2L;

        // When
        String result = integratedTestService.getCategoryCountAndExternalPost(genderFilter, postId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("활성 카테고리 수:");
        assertThat(result).contains("외부 포스트 제목:");
        
        System.out.println("성별 필터 테스트 결과: " + result);
    }
}
