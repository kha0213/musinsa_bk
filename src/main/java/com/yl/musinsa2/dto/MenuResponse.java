package com.yl.musinsa2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 응답 DTO - 무신사 스타일의 카테고리 메뉴")
public class MenuResponse {
    
    @Schema(description = "응답 데이터")
    private MenuData data;
    
    @Schema(description = "메타 정보")
    private MetaInfo meta;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "메뉴 데이터")
    public static class MenuData {
        
        @Schema(description = "카테고리 목록")
        private List<CategoryMenu> list;
        
        @Schema(description = "탭 목록")
        private List<TabInfo> tabs;
        
        @Schema(description = "성별 필터")
        private List<GenderFilter> gender;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "카테고리 메뉴")
    public static class CategoryMenu {
        
        @Schema(description = "카테고리 코드", example = "001")
        private String code;
        
        @Schema(description = "카테고리 제목", example = "상의")
        private String title;
        
        @Schema(description = "스토어 코드", example = "")
        private String storeCode;
        
        @Schema(description = "스토어 제목", example = "")
        private String storeTitle;
        
        @Schema(description = "스토어 아이콘 이미지 URL")
        private String storeIconImage;
        
        @Schema(description = "스토어 링크 URL")
        private String storeLinkUrl;
        
        @Schema(description = "링크 URL 제목", example = "전체 보기")
        private String linkUrlTitle;
        
        @Schema(description = "링크 URL")
        private String linkUrl;
        
        @Schema(description = "하위 카테고리 그룹 목록")
        private List<CategoryGroup> list;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "카테고리 그룹")
    public static class CategoryGroup {
        
        @Schema(description = "그룹 제목", example = "품목별")
        private String title;
        
        @Schema(description = "카테고리 아이템 목록")
        private List<CategoryItem> list;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "카테고리 아이템")
    public static class CategoryItem {
        
        @Schema(description = "카테고리 코드", example = "001001")
        private String code;
        
        @Schema(description = "카테고리 제목", example = "반소매 티셔츠")
        private String title;
        
        @Schema(description = "링크 URL")
        private String linkUrl;
        
        @Schema(description = "썸네일 이미지 URL")
        private String thumbnail;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "탭 정보")
    public static class TabInfo {
        
        @Schema(description = "탭 제목", example = "카테고리")
        private String title;
        
        @Schema(description = "탭 ID", example = "category")
        private String id;
        
        @Schema(description = "선택 여부", example = "true")
        private boolean selected;
        
        @Schema(description = "강조 표시 여부", example = "false")
        private boolean isEmphasis;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "성별 필터")
    public static class GenderFilter {
        
        @Schema(description = "성별 제목", example = "전체")
        private String title;
        
        @Schema(description = "성별 키", example = "A")
        private String key;
        
        @Schema(description = "선택 여부", example = "true")
        private boolean selected;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "메타 정보")
    public static class MetaInfo {
        
        @Schema(description = "결과 상태", example = "SUCCESS")
        private String result;
    }
}
