package com.yl.musinsa2.unit.dto;

import com.yl.musinsa2.dto.CategoryDto;
import com.yl.musinsa2.dto.CategoryResponse;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DTO 변환 단위테스트")
class CategoryDtoTest {

    @Test
    @DisplayName("Category 엔티티를 CategoryDto로 변환")
    void convertEntityToDto() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Category category = Category.builder()
                .id(1L)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .code("TEST001")
                .storeCode("STORE_TEST")
                .storeTitle("스토어 제목")
                .groupTitle("그룹 제목")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .build();

        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        // when
        CategoryDto dto = CategoryDto.from(category);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("테스트 카테고리");
        assertThat(dto.getDescription()).isEqualTo("테스트 설명");
        assertThat(dto.getCode()).isEqualTo("TEST001");
        assertThat(dto.getStoreCode()).isEqualTo("STORE_TEST");
        assertThat(dto.getStoreTitle()).isEqualTo("스토어 제목");
        assertThat(dto.getGroupTitle()).isEqualTo("그룹 제목");
        assertThat(dto.getDisplayOrder()).isEqualTo(1);
        assertThat(dto.getGenderFilter()).isEqualTo(GenderFilter.ALL);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
        assertThat(dto.getSortOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("부모가 있는 Category를 CategoryDto로 변환")
    void convertEntityWithParentToDto() {
        // given
        Category parent = Category.builder()
                .id(1L)
                .name("부모 카테고리")
                .build();

        Category child = Category.builder()
                .id(2L)
                .name("자식 카테고리")
                .parent(parent)
                .build();

        // when
        CategoryDto dto = CategoryDto.from(child);

        // then
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("자식 카테고리");
        assertThat(dto.getParentId()).isEqualTo(1L);
        assertThat(dto.getParentName()).isEqualTo("부모 카테고리");
    }

    @Test
    @DisplayName("자식이 있는 Category를 CategoryDto로 변환 (자식 포함)")
    void convertEntityWithChildrenToDto() {
        // given
        Category parent = Category.builder()
                .id(1L)
                .name("부모 카테고리")
                .children(new ArrayList<>())
                .build();

        Category child1 = Category.builder()
                .id(2L)
                .name("자식1")
                .parent(parent)
                .build();

        Category child2 = Category.builder()
                .id(3L)
                .name("자식2")
                .parent(parent)
                .build();

        parent.addChild(child1);
        parent.addChild(child2);

        // when
        CategoryDto dto = CategoryDto.fromWithChildren(parent);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("부모 카테고리");
        assertThat(dto.getChildren()).hasSize(2);
        assertThat(dto.getChildren().get(0).getName()).isEqualTo("자식1");
        assertThat(dto.getChildren().get(1).getName()).isEqualTo("자식2");
    }

    @Test
    @DisplayName("CategoryDto를 CategoryResponse로 변환")
    void convertDtoToResponse() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .code("TEST001")
                .displayOrder(1)
                .genderFilter(GenderFilter.ALL)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // when
        CategoryResponse response = CategoryResponse.convertToResponse(dto);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트 카테고리");
        assertThat(response.getDescription()).isEqualTo("테스트 설명");
        assertThat(response.getCode()).isEqualTo("TEST001");
        assertThat(response.getDisplayOrder()).isEqualTo(1);
        assertThat(response.getGenderFilter()).isEqualTo(GenderFilter.ALL);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
        assertThat(response.isRoot()).isTrue(); // parentId가 null이므로
        assertThat(response.isLeaf()).isTrue(); // children이 비어있으므로
    }

    @Test
    @DisplayName("Category 엔티티를 CategoryResponse로 직접 변환")
    void convertEntityToResponse() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .build();

        // when
        CategoryResponse response = CategoryResponse.from(category);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트 카테고리");
        assertThat(response.getDescription()).isEqualTo("테스트 설명");
    }

    @Test
    @DisplayName("displayOrder가 null인 경우 sortOrder는 0으로 설정")
    void convertEntityWithNullDisplayOrderToDto() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("테스트 카테고리")
                .displayOrder(null)
                .build();

        // when
        CategoryDto dto = CategoryDto.from(category);

        // then
        assertThat(dto.getDisplayOrder()).isNull();
        assertThat(dto.getSortOrder()).isEqualTo(0);
    }
}
