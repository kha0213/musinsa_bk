package com.yl.musinsa2.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SoftDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(exclude = {"parent", "children"})
@SoftDelete(columnName = "deleted")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name = "code", unique = true, length = 20)
    private String code;

    @Column(name = "store_code", length = 50)
    private String storeCode;

    @Column(name = "store_title", length = 100)
    private String storeTitle;

    @Column(name = "group_title", length = 100)
    private String groupTitle;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "gender_filter", length = 10)
    @Convert(converter = GenderFilterConverter.class)
    @Builder.Default
    private GenderFilter genderFilter = GenderFilter.ALL;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.genderFilter = GenderFilter.ALL;
    }

    public Category(String name, String description, Category parent) {
        this(name, description);
        this.parent = parent;
    }

    // 유틸리티 메서드들
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }

    // 루트 카테고리인지 확인
    public boolean isRoot() {
        return parent == null;
    }

    // 리프 카테고리인지 확인 (자식이 없는지)
    public boolean isLeaf() {
        return children.isEmpty();
    }

    // linkUrl getter - 항상 자동 생성
    public String getLinkUrl() {
        if (id != null) {
            return "/category/" + id;
        }
        return null;
    }

    // 스토어 관련 정보들을 위한 getter들
    public String getStoreIconImage() {
        return null; // 제거됨
    }

    public String getStoreLinkUrl() {
        return null; // 제거됨  
    }

    public String getLinkUrlTitle() {
        return "전체 보기"; // 기본값 반환
    }
}
