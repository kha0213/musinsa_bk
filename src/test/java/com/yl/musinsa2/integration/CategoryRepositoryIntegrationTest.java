package com.yl.musinsa2.integration;

import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import com.yl.musinsa2.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository 통합테스트")
class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 저장 및 조회")
    void saveAndFindCategory() {
        // given
        Category category = Category.builder()
                .name("테스트 카테고리")
                .description("테스트 설명")
                .code("TEST001")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .build();

        // when
        Category saved = categoryRepository.save(category);
        Optional<Category> found = categoryRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트 카테고리");
        assertThat(found.get().getCode()).isEqualTo("TEST001");
    }

    @Test
    @DisplayName("부모-자식 관계 저장 및 조회")
    void saveAndFindParentChildRelation() {
        // given
        Category parent = Category.builder()
                .name("부모 카테고리")
                .code("PARENT")
                .build();

        Category child = Category.builder()
                .name("자식 카테고리")
                .code("CHILD")
                .parent(parent)
                .build();

        // when
        categoryRepository.save(parent);
        categoryRepository.save(child);

        // then
        Optional<Category> foundChild = categoryRepository.findById(child.getId());
        assertThat(foundChild).isPresent();
        assertThat(foundChild.get().getParent()).isNotNull();
        assertThat(foundChild.get().getParent().getName()).isEqualTo("부모 카테고리");
    }

    @Test
    @DisplayName("부모가 없는 루트 카테고리 조회")
    void findRootCategories() {
        // given
        Category root1 = Category.builder()
                .name("루트1")
                .code("ROOT1")
                .build();

        Category root2 = Category.builder()
                .name("루트2")
                .code("ROOT2")
                .build();

        Category child = Category.builder()
                .name("자식")
                .code("CHILD")
                .parent(root1)
                .build();

        categoryRepository.save(root1);
        categoryRepository.save(root2);
        categoryRepository.save(child);

        // when
        List<Category> rootCategories = categoryRepository.findByParentIsNull();

        // then
        assertThat(rootCategories).hasSize(2);
        assertThat(rootCategories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("루트1", "루트2");
    }

    @Test
    @DisplayName("Soft Delete 테스트 - 삭제된 카테고리는 조회되지 않음")
    void softDeleteTest() {
        // given
        Category category = Category.builder()
                .name("삭제될 카테고리")
                .code("DELETE_TEST")
                .build();

        Category saved = categoryRepository.save(category);
        Long categoryId = saved.getId();

        // when - Soft Delete 실행
        categoryRepository.delete(saved);

        // then - 삭제된 카테고리는 findById로 조회되지 않음
        Optional<Category> found = categoryRepository.findById(categoryId);
        assertThat(found).isEmpty();

        // 전체 카테고리 목록에도 포함되지 않음
        List<Category> allCategories = categoryRepository.findAll();
        assertThat(allCategories)
                .extracting(Category::getId)
                .doesNotContain(categoryId);
    }

    @Test
    @DisplayName("CASCADE 동작 테스트")
    void cascadeTest() {
        // given
        Category parent = Category.builder()
                .name("부모")
                .code("PARENT")
                .build();

        Category child1 = Category.builder()
                .name("자식1")
                .code("CHILD1")
                .build();

        Category child2 = Category.builder()
                .name("자식2")
                .code("CHILD2")
                .build();

        parent.addChild(child1);
        parent.addChild(child2);

        // when
        Category savedParent = categoryRepository.save(parent);

        // then
        Optional<Category> foundParent = categoryRepository.findById(savedParent.getId());
        assertThat(foundParent).isPresent();
        assertThat(foundParent.get().getChildren()).hasSize(2);
        
        // 자식들도 저장되었는지 확인
        assertThat(foundParent.get().getChildren())
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("자식1", "자식2");
    }

    @Test
    @DisplayName("고유 제약조건 테스트 - 같은 code로 카테고리 생성 시 예외")
    void uniqueConstraintTest() {
        // given
        Category category1 = Category.builder()
                .name("카테고리1")
                .code("DUPLICATE")
                .build();

        Category category2 = Category.builder()
                .name("카테고리2")
                .code("DUPLICATE") // 같은 코드
                .build();

        // when & then
        categoryRepository.save(category1);
        
        assertThatThrownBy(() -> {
            categoryRepository.save(category2);
            categoryRepository.flush(); // 즉시 DB에 반영하여 제약조건 확인
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("JPA Auditing 테스트 - 생성일시, 수정일시 자동 설정")
    void auditingTest() {
        // given
        Category category = Category.builder()
                .name("오디팅 테스트")
                .code("AUDIT")
                .build();

        // when
        Category saved = categoryRepository.save(category);

        // then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());

        // 수정 테스트
        saved.setName("수정된 이름");
        Category updated = categoryRepository.save(saved);

        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }
}
