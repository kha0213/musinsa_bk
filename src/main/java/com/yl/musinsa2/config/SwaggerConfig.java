package com.yl.musinsa2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MUSINSA 카테고리 API",
                version = "1.0.0",
                description = """
                        ## MUSINSA 과제 - 온라인 쇼핑몰 카테고리 관리 시스템
                        
                        이 API는 온라인 쇼핑몰의 상품 카테고리를 관리하기 위한 RESTful API입니다.
                        트리 구조의 카테고리를 생성, 조회, 수정, 삭제할 수 있습니다.
                        
                        ### 주요 기능
                        - 📂 **트리 구조 카테고리 관리**: 부모-자식 관계를 통한 계층적 구조
                        - 🔍 **다양한 조회 옵션**: 전체, 트리, 개별, 자식 카테고리 조회
                        - 🔎 **검색 기능**: 카테고리명으로 검색
                        - 📊 **통계 기능**: 카테고리 개수 및 구조 통계
                        - 🗑️ **안전한 삭제**: 논리적/물리적 삭제 옵션
                        - ✅ **상태 관리**: 활성화/비활성화 토글
                        
                        ### 기술 스택
                        - Spring Boot 3.5.5, JPA, H2 Database
                        - Lombok, Bean Validation, JPA Auditing
                        """,
                contact = @Contact(
                        name = "MUSINSA 개발팀",
                        email = "dev@musinsa.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "로컬 개발 서버"
                )
        }
)
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
