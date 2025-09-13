package com.yl.musinsa2.unit.entity;

import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Category 엔티티 단위테스트")
class CategoryTest {

    @Test
    @DisplayName("카테고리 생성 시 기본값이 설정된다")
    void createCategoryWithDefaults() {
        // given & when
        Category category = Category.builder()
                .name("테스트 카테고리")
                .description("테스트 설명")
                .build();

        // then
        assertThat(category.getName()).isEqualTo("테스트 카테고리");
        assertThat(category.getDescription()).isEqualTo("테스트 설명");
        assertThat(category.getDisplayOrder()).isEqualTo(0);
        assertThat(category.getGenderFilter()).isEqualTo(GenderFilter.ALL);
        assertThat(category.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("루트 카테고리 여부를 올바르게 판단한다")
    void isRootCategory() {
        // given
        Category rootCategory = Category.builder()
                .name("루트 카테고리")
                .build();

        Category childCategory = Category.builder()
                .name("자식 카테고리")
                .parent(rootCategory)
                .build();

        // when & then
        assertThat(rootCategory.isRoot()).isTrue();
        assertThat(childCategory.isRoot()).isFalse();
    }

    @Test
    @DisplayName("리프 카테고리 여부를 올바르게 판단한다")
    void isLeafCategory() {
        // given
        Category parentCategory = Category.builder()
                .name("부모 카테고리")
                .build();

        Category childCategory = Category.builder()
                .name("자식 카테고리")
                .build();

        // when
        parentCategory.addChild(childCategory);

        // then
        assertThat(parentCategory.isLeaf()).isFalse();
        assertThat(childCategory.isLeaf()).isTrue();
    }

    @Test
    @DisplayName("자식 카테고리 추가 시 양방향 관계가 설정된다")
    void addChildCategory() {
        // given
        Category parent = Category.builder()
                .name("부모 카테고리")
                .build();

        Category child = Category.builder()
                .name("자식 카테고리")
                .build();

        // when
        parent.addChild(child);

        // then
        assertThat(parent.getChildren()).hasSize(1);
        assertThat(parent.getChildren()).contains(child);
        assertThat(child.getParent()).isEqualTo(parent);
    }

    @Test
    @DisplayName("자식 카테고리 제거 시 양방향 관계가 해제된다")
    void removeChildCategory() {
        // given
        Category parent = Category.builder()
                .name("부모 카테고리")
                .build();

        Category child = Category.builder()
                .name("자식 카테고리")
                .build();

        parent.addChild(child);

        // when
        parent.removeChild(child);

        // then
        assertThat(parent.getChildren()).isEmpty();
        assertThat(child.getParent()).isNull();
    }

    @Test
    @DisplayName("링크 URL이 올바르게 생성된다")
    void generateLinkUrl() {
        // given
        Category category = Category.builder()
                .name("테스트 카테고리")
                .build();

        // when - ID가 없는 경우
        String urlWithoutId = category.getLinkUrl();

        // ID를 직접 설정 (실제로는 JPA가 설정)
        category.setId(1L);
        String urlWithId = category.getLinkUrl();

        // then
        assertThat(urlWithoutId).isNull();
        assertThat(urlWithId).isEqualTo("/category/1");
    }

    @Test
    @DisplayName("생성자를 통한 카테고리 생성이 올바르게 동작한다")
    void createCategoryWithConstructor() {
        // given
        Category parent = Category.builder()
                .name("부모 카테고리")
                .build();

        // when
        Category simpleCategory = new Category("간단 카테고리", "간단 설명");
        Category categoryWithParent = new Category("자식 카테고리", "자식 설명", parent);

        // then
        assertThat(simpleCategory.getName()).isEqualTo("간단 카테고리");
        assertThat(simpleCategory.getDescription()).isEqualTo("간단 설명");
        assertThat(simpleCategory.getGenderFilter()).isEqualTo(GenderFilter.ALL);

        assertThat(categoryWithParent.getName()).isEqualTo("자식 카테고리");
        assertThat(categoryWithParent.getDescription()).isEqualTo("자식 설명");
        assertThat(categoryWithParent.getParent()).isEqualTo(parent);
        assertThat(categoryWithParent.getGenderFilter()).isEqualTo(GenderFilter.ALL);
    }
}
