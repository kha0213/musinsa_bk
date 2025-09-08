# MUSINSA 과제 - 온라인 쇼핑몰 카테고리 시스템

## 프로젝트 개요
MUSINSA 과제를 위한 온라인 쇼핑몰의 상품 카테고리 구현 프로젝트입니다.
트리 구조의 카테고리를 관리할 수 있는 REST API를 제공합니다.

## 기술 스택
- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **H2 Database** (메모리 DB)
- **Gradle**
- **Lombok** (코드 간소화)
- **Bean Validation** (입력값 검증)
- **SLF4J + Logback** (로깅)

## 주요 기능

### [제출과제]
✅ 온라인 쇼핑몰의 상품 카테고리를 구현하세요

### [개발시 반드시 포함되어야 하는 항목]
✅ **RDBMS**: 과제 작성시 Embedded DB 사용 권장 (예: H2 DB)
✅ **REST API**: 완전한 REST API 구현

### [개발시 반드시 구현되어야 하는 내용]
✅ **카테고리 등록/수정/삭제 API**
- 카테고리를 등록/수정/삭제 할 수 있어야 합니다
- 카테고리 조회 시 자기 자신을 포함해 하위 카테고리 조회가 가능해야 합니다
- 카테고리를 지정하지 않을 시, 전체 카테고리를 반환해야 합니다
- 카테고리는 트리 구조로 반환해야 합니다

✅ **카테고리 조회 API**
- 카테고리 조회 시 자기 자신을 포함해 하위 카테고리 조회가 가능해야 합니다
- 카테고리를 지정하지 않을 시, 전체 카테고리를 반환해야 합니다
- 카테고리는 트리 구조로 반환해야 합니다

## API 명세

### HTTP 상태 코드
| 상태 코드 | 설명 | 사용 예시 |
|----------|------|----------|
| `200 OK` | 성공적인 조회/수정 | GET, PUT, PATCH |
| `201 Created` | 리소스 생성 성공 | POST |
| `204 No Content` | 성공적인 삭제 | DELETE |
| `400 Bad Request` | 잘못된 요청/유효성 오류 | 입력값 오류 |
| `404 Not Found` | 리소스를 찾을 수 없음 | 존재하지 않는 ID |
| `500 Internal Server Error` | 서버 내부 오류 | 예상치 못한 오류 |

### 1. 카테고리 조회
```bash
# 모든 카테고리 조회
GET /api/categories

# 트리 구조로 카테고리 조회
GET /api/categories/tree

# 특정 카테고리 조회 (하위 카테고리 포함)
GET /api/categories/{id}

# 특정 부모의 자식 카테고리들 조회
GET /api/categories/{parentId}/children

# 카테고리 검색
GET /api/categories/search?name={name}

# 카테고리 통계
GET /api/categories/statistics
```

### 2. 카테고리 등록
```bash
POST /api/categories
Content-Type: application/json
# 응답: 201 Created

{
  "name": "카테고리명",
  "description": "카테고리 설명",
  "parentId": 1  // 부모 카테고리 ID (선택사항)
}
```

### 3. 카테고리 수정
```bash
PUT /api/categories/{id}
Content-Type: application/json
# 응답: 200 OK

{
  "name": "수정된 카테고리명",
  "description": "수정된 카테고리 설명",
  "isActive": true
}
```

### 4. 카테고리 삭제
```bash
# 논리적 삭제 (비활성화) - 204 No Content
DELETE /api/categories/{id}

# 물리적 삭제 (완전 삭제) - 204 No Content
DELETE /api/categories/{id}/permanent

# 카테고리 상태 토글 (활성화/비활성화) - 200 OK
PATCH /api/categories/{id}/toggle-status
```

## 데이터베이스 설계

### Category 테이블
```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);
```

## 프로젝트 구조
```
src/main/java/com/yl/musinsa2/
├── Musinsa2Application.java          # 메인 애플리케이션 클래스
├── entity/
│   └── Category.java                 # 카테고리 엔티티
├── repository/
│   └── CategoryRepository.java       # 카테고리 레포지토리
├── dto/
│   ├── CategoryCreateRequest.java    # 카테고리 생성 요청 DTO
│   ├── CategoryUpdateRequest.java    # 카테고리 수정 요청 DTO
│   └── CategoryResponse.java         # 카테고리 응답 DTO
├── service/
│   └── CategoryService.java          # 카테고리 서비스
├── controller/
│   └── CategoryController.java       # 카테고리 컨트롤러
└── exception/
    └── GlobalExceptionHandler.java  # 글로벌 예외 처리

src/main/resources/
├── application.yml                # YAML 설정 파일
├── schema.sql                     # 데이터베이스 스키마 정의
└── data.sql                       # 초기 데이터 삽입

src/test/java/
└── service/CategoryServiceTest.java  # 단위 테스트
```
├── dto/
│   ├── CategoryCreateRequest.java    # 카테고리 생성 요청 DTO
│   ├── CategoryUpdateRequest.java    # 카테고리 수정 요청 DTO
│   └── CategoryResponse.java         # 카테고리 응답 DTO
├── service/
│   └── CategoryService.java          # 카테고리 서비스
├── controller/
│   └── CategoryController.java       # 카테고리 컨트롤러
├── exception/
│   └── GlobalExceptionHandler.java  # 글로벌 예외 처리
└── config/
    └── DataInitializer.java          # 초기 데이터 설정
```

## 실행 방법

### 1. 프로젝트 클론 및 빌드
```bash
# 프로젝트 디렉토리로 이동
cd C:\Long\musinsa2

# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 2. H2 데이터베이스 콘솔 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (빈 문자열)

### 3. API 테스트
애플리케이션이 실행되면 다음 URL에서 API를 테스트할 수 있습니다:
- Base URL: http://localhost:8080/api/categories

## 초기 데이터

애플리케이션 실행 시 `data.sql` 파일을 통해 **데이터가 없을 때만** 다음과 같은 카테고리 구조가 자동으로 생성됩니다:

> 📝 **참고**: `spring.sql.init.mode: embedded` 설정으로 인해 H2 메모리 DB에서만 실행되며, 이미 데이터가 있으면 중복 삽입을 방지합니다.

```
남성
├── 상의
│   ├── 티셔츠
│   ├── 셔츠
│   ├── 니트/스웨터
│   └── 후드티
├── 하의
│   ├── 진
│   ├── 팬츠
│   ├── 반바지
│   └── 조거팬츠
└── 아우터

여성
├── 상의
│   ├── 티셔츠
│   ├── 블라우스
│   └── 니트/가디건
├── 하의
├── 원피스
└── 아우터

키즈

신발
├── 스니커즈
├── 구두
├── 부츠
└── 샌들

액세서리
├── 가방
├── 시계
├── 지갑
└── 주얼리
```

## API 사용 예시

### 1. 전체 카테고리 트리 조회
```bash
curl -X GET "http://localhost:8080/api/categories/tree"
```

### 2. 새 카테고리 생성
```bash
curl -X POST "http://localhost:8080/api/categories" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "새 카테고리",
    "description": "새로운 카테고리입니다",
    "parentId": 1
  }'
```

### 3. 카테고리 수정
```bash
curl -X PUT "http://localhost:8080/api/categories/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 카테고리",
    "description": "수정된 설명",
    "isActive": true
  }'
```

### 4. 카테고리 검색
```bash
curl -X GET "http://localhost:8080/api/categories/search?name=티셔츠"
```

## 특징

1. **트리 구조 지원**: 부모-자식 관계를 통한 계층적 카테고리 구조
2. **논리적 삭제**: 데이터의 안정성을 위한 소프트 딜리트 기능
3. **유효성 검증**: Bean Validation을 활용한 입력값 검증
4. **예외 처리**: 글로벌 예외 처리를 통한 일관된 에러 응답
5. **초기 데이터**: 테스트를 위한 샘플 카테고리 자동 생성
6. **상세한 API**: CRUD 뿐만 아니라 검색, 통계, 상태 토글 등 다양한 기능
7. **Lombok 활용**: @Data, @Builder, @RequiredArgsConstructor 등으로 보일러플레이트 코드 최소화
8. **체계적인 로깅**: @Slf4j를 통한 구조화된 로그 관리
9. **YAML 설정**: 가독성 좋은 application.yml 설정 파일
10. **SQL 기반 초기화**: data.sql을 통한 체계적인 초기 데이터 관리
11. **완전한 테스트**: 단위 테스트와 통합 테스트 포함

## 문서화
- README 에 아래 내용을 명시 해 주세요
  - 애플리케이션을 실행하기 위해 필요한 설치 및 빌드 방법
  - Database 명세
  - API 명세
  - 직접 붙여도 되고, Swagger 나 Rest Docs 를 적용했다면, 접속 방법을 명시해도 됩니다.

---

**개발자**: MUSINSA 과제 수행자  
**개발 기간**: +7일
