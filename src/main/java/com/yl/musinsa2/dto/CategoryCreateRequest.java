package com.yl.musinsa2.dto;

import com.yl.musinsa2.entity.GenderFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 생성 요청 DTO")
public class CategoryCreateRequest {
    
    @Schema(
            description = "카테고리 이름",
            example = "스니커즈",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "카테고리 이름은 필수입니다")
    @Size(max = 100, message = "카테고리 이름은 100자를 초과할 수 없습니다")
    private String name;
    
    @Schema(
            description = "카테고리 설명",
            example = "운동화 및 캐주얼 스니커즈",
            maxLength = 500
    )
    @Size(max = 500, message = "카테고리 설명은 500자를 초과할 수 없습니다")
    private String description;
    
    @Schema(
            description = "부모 카테고리 ID (null이면 루트 카테고리)",
            example = "4"
    )
    private Long parentId;
    
    // 무신사 스타일 추가 필드들
    @Schema(description = "카테고리 코드", example = "001")
    private String code;
    
    @Schema(description = "스토어 코드")
    private String storeCode;
    
    @Schema(description = "스토어 제목")
    private String storeTitle;
    
    @Schema(description = "그룹 제목", example = "품목별")
    private String groupTitle;
    
    @Schema(description = "표시 순서", example = "0")
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Schema(description = "성별 필터", example = "ALL", allowableValues = {"ALL", "MALE", "FEMALE"})
    @Builder.Default
    private GenderFilter genderFilter = GenderFilter.ALL;
}
