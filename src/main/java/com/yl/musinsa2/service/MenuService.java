package com.yl.musinsa2.service;

import com.yl.musinsa2.dto.MenuResponse;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.entity.GenderFilter;
import com.yl.musinsa2.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final CategoryRepository categoryRepository;

    public MenuResponse getMenu(String tabId, String genderFilter) {
        log.info("메뉴 데이터 생성 시작 - tabId: {}, genderFilter: {}", tabId, genderFilter);
        
        // 활성화된 카테고리만 조회하고 성별 필터 적용
        List<Category> categories;
        GenderFilter genderFilterEnum = GenderFilter.fromCode(genderFilter);
        
        if (GenderFilter.ALL.equals(genderFilterEnum)) {
            // 전체: 모든 카테고리 조회
            categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrder();
        } else {
            // 특정 성별: 전체(ALL) 또는 해당 성별 카테고리만 조회
            categories = categoryRepository.findAllByIsActiveTrueAndGenderFilterIncludingAll(genderFilterEnum);
        }
        
        // 루트 카테고리들만 추출
        List<Category> rootCategories = categories.stream()
                .filter(Category::isRoot)
                .collect(Collectors.toList());
        
        // 카테고리 메뉴 생성
        List<MenuResponse.CategoryMenu> categoryMenus = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            MenuResponse.CategoryMenu categoryMenu = buildCategoryMenu(rootCategory, categories);
            categoryMenus.add(categoryMenu);
        }
        
        // 메뉴 데이터 구성
        MenuResponse.MenuData menuData = MenuResponse.MenuData.builder()
                .list(categoryMenus)
                .tabs(buildTabs(tabId))
                .gender(buildGenderFilters(genderFilter))
                .build();
        
        // 메타 정보
        MenuResponse.MetaInfo metaInfo = MenuResponse.MetaInfo.builder()
                .result("SUCCESS")
                .build();
        
        return MenuResponse.builder()
                .data(menuData)
                .meta(metaInfo)
                .build();
    }
    
    private MenuResponse.CategoryMenu buildCategoryMenu(Category rootCategory, List<Category> allCategories) {
        // 해당 루트 카테고리의 자식들을 찾기
        List<Category> children = allCategories.stream()
                .filter(cat -> rootCategory.equals(cat.getParent()))
                .collect(Collectors.toList());
        
        // 그룹별로 카테고리 분류
        Map<String, List<Category>> groupedChildren = children.stream()
                .collect(Collectors.groupingBy(cat -> 
                    cat.getGroupTitle() != null ? cat.getGroupTitle() : ""));
        
        // 카테고리 그룹 리스트 생성
        List<MenuResponse.CategoryGroup> categoryGroups = new ArrayList<>();
        for (Map.Entry<String, List<Category>> entry : groupedChildren.entrySet()) {
            String groupTitle = entry.getKey();
            List<Category> groupCategories = entry.getValue();
            
            List<MenuResponse.CategoryItem> categoryItems = groupCategories.stream()
                    .map(this::buildCategoryItem)
                    .collect(Collectors.toList());
            
            MenuResponse.CategoryGroup categoryGroup = MenuResponse.CategoryGroup.builder()
                    .title(groupTitle)
                    .list(categoryItems)
                    .build();
            
            categoryGroups.add(categoryGroup);
        }
        
        return MenuResponse.CategoryMenu.builder()
                .code(rootCategory.getCode() != null ? rootCategory.getCode() : rootCategory.getId().toString())
                .title(rootCategory.getName())
                .storeCode(rootCategory.getStoreCode() != null ? rootCategory.getStoreCode() : "")
                .storeTitle(rootCategory.getStoreTitle() != null ? rootCategory.getStoreTitle() : "")
                .storeIconImage("") // 제거됨
                .storeLinkUrl("") // 제거됨
                .linkUrlTitle("전체 보기") // 기본값
                .linkUrl(rootCategory.getLinkUrl())
                .list(categoryGroups)
                .build();
    }
    
    private MenuResponse.CategoryItem buildCategoryItem(Category category) {
        return MenuResponse.CategoryItem.builder()
                .code(category.getCode() != null ? category.getCode() : category.getId().toString())
                .title(category.getName())
                .linkUrl(category.getLinkUrl() != null ? category.getLinkUrl() : "/category/" + category.getId())
                .thumbnail(null) // thumbnail 제거
                .build();
    }
    
    private List<MenuResponse.TabInfo> buildTabs(String selectedTabId) {
        List<MenuResponse.TabInfo> tabs = new ArrayList<>();
        
        tabs.add(MenuResponse.TabInfo.builder()
                .title("카테고리")
                .id("category")
                .selected("category".equals(selectedTabId))
                .isEmphasis(false)
                .build());
        
        tabs.add(MenuResponse.TabInfo.builder()
                .title("브랜드")
                .id("brand")
                .selected("brand".equals(selectedTabId))
                .isEmphasis(true)
                .build());
        
        tabs.add(MenuResponse.TabInfo.builder()
                .title("서비스")
                .id("service")
                .selected("service".equals(selectedTabId))
                .isEmphasis(false)
                .build());
        
        return tabs;
    }
    
    private List<MenuResponse.GenderFilter> buildGenderFilters(String selectedGender) {
        List<MenuResponse.GenderFilter> genderFilters = new ArrayList<>();
        
        genderFilters.add(MenuResponse.GenderFilter.builder()
                .title("전체")
                .key("A")
                .selected("A".equals(selectedGender))
                .build());
        
        genderFilters.add(MenuResponse.GenderFilter.builder()
                .title("남성")
                .key("M")
                .selected("M".equals(selectedGender))
                .build());
        
        genderFilters.add(MenuResponse.GenderFilter.builder()
                .title("여성")
                .key("F")
                .selected("F".equals(selectedGender))
                .build());
        
        return genderFilters;
    }
}
