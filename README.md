# Musinsa

카테고리 관리 시스템과 모니터링 스택

## 시스템 실행 방법

### 전체 시스템 실행

```bash
docker-compose up -d
```

### 개별 서비스 실행

```bash
# 애플리케이션만 실행
docker-compose up -d app redis

# 모니터링 스택만 실행
docker-compose up -d prometheus grafana loki promtail redis-exporter redisinsight
```

### 시스템 종료

```bash
# 전체 종료
docker-compose down

# 데이터 볼륨 포함 완전 삭제
docker-compose down -v
```

## 접속 URL

| 서비스                 | URL                                   | 인증 정보                                                      |
|---------------------|---------------------------------------|------------------------------------------------------------|
| API Server          | http://localhost:8080                 | -                                                          |
| Swagger UI          | http://localhost:8080/swagger-ui.html | -                                                          |
| H2 Database Console | http://localhost:8080/h2-console      | JDBC URL: jdbc:h2:mem:testdb, User: sa, Password: (empty)  |
| Grafana Dashboard   | http://localhost:3000                 | admin / admin123                                           |
| Prometheus          | http://localhost:9090                 | -                                                          |
| RedisInsight        | http://localhost:5540                 | Database Alias : 127.0.0.1:6379, Host : redis, Port : 6379 |

## Database 스키마

### Category Table

```sql
-- 카테고리 테이블 스키마
CREATE TABLE IF NOT EXISTS categories
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    name
    VARCHAR
(
    100
) NOT NULL,
    description VARCHAR
(
    500
),
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIME,
    updated_at TIMESTAMP DEFAULT CURRENT_TIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    code VARCHAR
(
    20
) UNIQUE,
    store_code VARCHAR
(
    50
),
    store_title VARCHAR
(
    100
),
    group_title VARCHAR
(
    100
),
    display_order INTEGER NOT NULL DEFAULT 0,
    gender_filter VARCHAR
(
    10
) NOT NULL DEFAULT 'A', -- A: 전체, M: 남성, F: 여성

    FOREIGN KEY
(
    parent_id
) REFERENCES categories
(
    id
)
    );

CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON categories(parent_id, display_order);
CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(name);
CREATE INDEX IF NOT EXISTS idx_categories_code ON categories(code);
CREATE INDEX IF NOT EXISTS idx_categories_parent_not_deleted ON categories(parent_id, deleted);

```

### 애플리케이션 서비스

**musinsa-app**

- Spring Boot 3.5.5 기반 카테고리 관리 API 서버
- H2 In-memory 데이터베이스 사용
- Redis 캐싱 적용
- RESTful API 제공

**musinsa-redis**

- 캐시 데이터 저장소
- 카테고리 조회 성능 최적화
- 메모리 제한: 256MB

### 모니터링 서비스

**musinsa-prometheus**

- 메트릭 수집 및 저장
- Spring Boot Actuator로부터 애플리케이션 메트릭 수집
- 데이터 보존 기간: 200시간

**musinsa-grafana**

- 메트릭 시각화 및 대시보드 제공
- Prometheus 데이터소스 연동
- 사전 구성된 API 모니터링 대시보드

**musinsa-redis-exporter**

- Redis 메트릭 수집
- Prometheus 포맷으로 Redis 성능 지표 제공
- 연결 상태, 메모리 사용량, 명령어 통계

**musinsa-redisinsight**

- Redis 데이터 시각화 및 관리 도구
- 키-값 데이터 조회 및 편집
- 실시간 모니터링 및 성능 분석

## API 엔드포인트

| Method | URL                  | 설명             |
|--------|----------------------|----------------|
| GET    | /api/categories      | 전체 카테고리 조회     |
| GET    | /api/categories/{id} | 특정 카테고리 조회     |
| POST   | /api/categories      | 카테고리 생성        |
| PUT    | /api/categories/{id} | 카테고리 수정        |
| DELETE | /api/categories/{id} | 카테고리 삭제        |
| GET    | /actuator/health     | 애플리케이션 상태 확인   |
| GET    | /actuator/prometheus | Prometheus 메트릭 |

## 디렉토리 구조

```
musinsa2/
├── src/main/java/com/yl/musinsa2/          # 애플리케이션 소스
├── monitoring/                             # 모니터링 설정
│   ├── prometheus/prometheus.yml           # Prometheus 설정
│   └── grafana/provisioning/               # Grafana 자동 설정
├── logs/                                  # 애플리케이션 로그
├── docker-compose.yml                     # 전체 서비스 정의
└── Dockerfile                             # 애플리케이션 컨테이너 이미지
```
