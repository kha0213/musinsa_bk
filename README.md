# MUSINSA 스타일 카테고리 API

무신사(MUSINSA)의 카테고리 메뉴 API 구조를 참고하여 개발한 Spring Boot 기반의 카테고리 관리 시스템입니다.

## 🚀 주요 기능

### 0. OpenFeign & QueryDSL 통합
- **OpenFeign**: 외부 API 호출을 위한 선언적 HTTP 클라이언트
- **QueryDSL**: 타입 안전한 SQL 쿼리 빌더
- 통합 테스트 API 제공

### 1. 무신사 스타일 메뉴 API
- **엔드포인트**: `GET /api2/dp/v4/menu`
- 무신사와 동일한 JSON 구조 제공
- 성별 필터링 지원 (전체/남성/여성)
- 탭 시스템 (카테고리/브랜드/서비스)
- 계층적 카테고리 구조

### 2. 기존 카테고리 CRUD API (하위 호환성 보장)
- **기본 엔드포인트**: `/api/categories`
- 전체 카테고리 조회, 생성, 수정, 삭제
- 카테고리 트리 구조 조회
- 검색 및 통계 기능

## 📋 API 엔드포인트

### 무신사 스타일 API
```
GET /api2/dp/v4/menu?tabId=category&gf=A
```

**파라미터:**
- `tabId`: 탭 ID (category, brand, service)
- `gf`: 성별 필터 (A: 전체, M: 남성, F: 여성)

**응답 예시:**
```json
{
  "data": {
    "list": [
      {
        "code": "001",
        "title": "상의",
        "storeCode": "",
        "storeTitle": "",
        "storeIconImage": "",
        "storeLinkUrl": "",
        "linkUrlTitle": "전체 보기",
        "linkUrl": "/category/001",
        "list": [
          {
            "title": "",
            "list": [
              {
                "code": "001001",
                "title": "반소매 티셔츠",
                "linkUrl": "/category/001001",
                "thumbnail": "https://image.msscdn.net/images/category_img/men/ITEM_001001_17507308412077_big.png"
              }
            ]
          }
        ]
      }
    ],
    "tabs": [
      {
        "title": "카테고리",
        "id": "category",
        "selected": true,
        "isEmphasis": false
      }
    ],
    "gender": [
      {
        "title": "전체",
        "key": "A",
        "selected": true
      }
    ]
  },
  "meta": {
    "result": "SUCCESS"
  }
}
```

### 기존 카테고리 API
```
GET    /api/categories              # 전체 카테고리 조회
GET    /api/categories/tree         # 트리 구조 조회
GET    /api/categories/{id}         # 특정 카테고리 조회
POST   /api/categories              # 카테고리 생성
PUT    /api/categories/{id}         # 카테고리 수정
DELETE /api/categories/{id}         # 카테고리 삭제
GET    /api/categories/search       # 카테고리 검색
GET    /api/categories/statistics   # 통계 조회
```

### OpenFeign 테스트 API
```
GET    /api/posts/{id}              # 외부 API 단일 포스트 조회
GET    /api/posts                   # 외부 API 전체 포스트 조회
```

### QueryDSL 테스트 API
```
GET    /api/test/querydsl/search    # 복합 조건 검색
GET    /api/test/querydsl/roots     # 루트 카테고리 조회
GET    /api/test/querydsl/count     # 활성 카테고리 개수
GET    /api/test/querydsl/group/{groupTitle}  # 그룹별 조회
```

### 통합 테스트 API
```
GET    /api/test/integration        # QueryDSL + OpenFeign 통합 테스트
```

## 🛠 기술 스택

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21
- **Database**: H2 Database (개발용)
- **ORM**: Spring Data JPA + QueryDSL 5.0.0
- **HTTP Client**: OpenFeign (Spring Cloud)
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Gradle
- **Testing**: JUnit 5, MockMvc

## 📦 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd musinsa2
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. API 문서 확인
애플리케이션 실행 후 브라우저에서 접속:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

## 🗃 데이터베이스 스키마

### categories 테이블
```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIME,
    updated_at TIMESTAMP DEFAULT CURRENT_TIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- 무신사 스타일 확장 필드
    code VARCHAR(20) UNIQUE,
    thumbnail_url VARCHAR(500),
    link_url VARCHAR(500),
    store_code VARCHAR(50),
    store_title VARCHAR(100),
    store_icon_image VARCHAR(500),
    store_link_url VARCHAR(500),
    link_url_title VARCHAR(100),
    group_title VARCHAR(100),
    display_order INTEGER NOT NULL DEFAULT 0,
    gender_filter VARCHAR(10) NOT NULL DEFAULT 'A',
    
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);
```

## 🏗 프로젝트 구조

```
src/main/java/com/yl/musinsa2/
├── controller/
│   ├── CategoryController.java    # 기존 카테고리 API
│   └── MenuController.java        # 무신사 스타일 메뉴 API
├── dto/
│   ├── MenuResponse.java          # 무신사 스타일 응답 DTO
│   ├── CategoryResponse.java      # 카테고리 응답 DTO
│   └── CategoryCreateRequest.java # 카테고리 생성 요청 DTO
├── entity/
│   └── Category.java              # 카테고리 엔티티 (확장됨)
├── repository/
│   └── CategoryRepository.java    # 카테고리 레포지토리
└── service/
    ├── CategoryService.java       # 카테고리 서비스
    └── MenuService.java          # 메뉴 서비스
```

## 🔧 주요 변경사항

### 1. Category 엔티티 확장
무신사 API 구조를 지원하기 위해 다음 필드들이 추가되었습니다:
- `code`: 카테고리 코드
- `storeCode`, `storeTitle`: 스토어 관련 정보
- `groupTitle`: 그룹 제목
- `displayOrder`: 표시 순서
- `genderFilter`: 성별 필터 (Enum: ALL, MALE, FEMALE)

### 2. 새로운 API 엔드포인트
`/api2/dp/v4/menu` 엔드포인트가 추가되어 무신사와 동일한 구조의 응답을 제공합니다.

### 3. 하위 호환성
기존 카테고리 API는 그대로 유지되어 기존 클라이언트와의 호환성을 보장합니다.

## 🧪 테스트

### 단위 테스트 실행
```bash
./gradlew test
```

### 통합 테스트
`MenuControllerIntegrationTest` 클래스에서 무신사 스타일 API의 통합 테스트를 수행합니다.

## 📝 샘플 데이터

애플리케이션 시작시 다음과 같은 샘플 데이터가 자동으로 생성됩니다:
- 상의 (반소매 티셔츠, 셔츠/블라우스, 후드 티셔츠 등)
- 아우터 (블루종/MA-1, 레더/라이더스 재킷 등)
- 바지 (데님 팬츠, 트레이닝/조거 팬츠 등)
- 신발 (스니커즈, 부츠/워커, 구두 등)
- 가방 (백팩, 메신저/크로스 백, 숄더백 등)
- 뷰티 (스킨케어, 마스크팩, 베이스메이크업 등)

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 🙋‍♂️ 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.
