package com.yl.musinsa2.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
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
@EqualsAndHashCode(of = "id")
public class Category extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    // 트리 구조를 위한 부모-자식 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> children = new ArrayList<>();
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    // 편의 생성자들
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
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
}
