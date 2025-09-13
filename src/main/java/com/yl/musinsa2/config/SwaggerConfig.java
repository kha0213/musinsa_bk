package com.yl.musinsa2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("MUSINSA 카테고리 API")
                        .version("1.0.0")
                        .description("온라인 쇼핑몰 카테고리 관리 시스템")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .components(new Components()
                        // 공통 응답 예시들
                        .addResponses("BadRequest", new ApiResponse()
                                .description("잘못된 요청")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .example("""
                                                        {
                                                          "success": false,
                                                          "message": "입력값 검증에 실패했습니다.",
                                                          "errors": {
                                                            "name": "카테고리 이름은 필수입니다"
                                                          },
                                                          "timestamp": "2024-01-15T10:30:00"
                                                        }
                                                        """)
                                        )
                                )
                        )
                        .addResponses("NotFound", new ApiResponse()
                                .description("리소스를 찾을 수 없음")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .example("""
                                                        {
                                                          "success": false,
                                                          "message": "요청한 리소스를 찾을 수 없습니다.",
                                                          "details": "카테고리를 찾을 수 없습니다. ID: 999",
                                                          "timestamp": "2024-01-15T10:30:00"
                                                        }
                                                        """)
                                        )
                                )
                        )
                        .addResponses("ServerError", new ApiResponse()
                                .description("서버 내부 오류")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .example("""
                                                        {
                                                          "success": false,
                                                          "message": "서버 내부 오류가 발생했습니다.",
                                                          "details": "시스템 관리자에게 문의하시기 바랍니다.",
                                                          "timestamp": "2024-01-15T10:30:00"
                                                        }
                                                        """)
                                        )
                                )
                        )
                );
    }
}
