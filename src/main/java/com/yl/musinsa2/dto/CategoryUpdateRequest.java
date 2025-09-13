package com.yl.musinsa2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 수정 요청 DTO")
public class CategoryUpdateRequest {

    @Schema(
            description = "카테고리 이름",
            example = "업데이트된 카테고리명",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "카테고리 이름은 필수입니다")
    @Size(max = 100, message = "카테고리 이름은 100자를 초과할 수 없습니다")
    private String name;

    @Schema(
            description = "카테고리 설명",
            example = "수정된 카테고리 설명",
            maxLength = 500
    )
    @Size(max = 500, message = "카테고리 설명은 500자를 초과할 수 없습니다")
    private String description;
}
