# Musinsa2 - Category Management System with Monitoring

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

| 서비스 | URL | 인증 정보 |
|---------|-----|-----------|
| API Server | http://localhost:8080 | - |
| Swagger UI | http://localhost:8080/swagger-ui.html | - |
| H2 Database Console | http://localhost:8080/h2-console | JDBC URL: jdbc:h2:mem:testdb, User: sa, Password: (empty) |
| Grafana Dashboard | http://localhost:3000 | admin / admin123 |
| Prometheus | http://localhost:9090 | - |
| Loki | http://localhost:3100 | - |
| RedisInsight | http://localhost:8001 | - |

## Database 스키마

### Category Table
```sql
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_category_id BIGINT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    gender_filter VARCHAR(10) NOT NULL DEFAULT 'ALL',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES category(id)
);
```

### 초기 데이터
- 남성 의류 (대분류)
  - 상의, 하의, 신발, 액세서리 (중분류)
- 여성 의류 (대분류)
  - 상의, 하의, 신발, 액세서리 (중분류)
- 공용 (대분류)
  - 가방, 시계 (중분류)

## 각 서비스별 역할

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
- Prometheus와 Loki 데이터소스 연동
- 사전 구성된 API 모니터링 대시보드

**musinsa-loki**
- 로그 데이터 저장소
- 구조화된 로그 검색 및 분석 지원
- 자동 로그 압축 및 인덱싱

**musinsa-promtail**
- 로그 수집 에이전트
- 애플리케이션 로그 파일을 Loki로 전송
- 로그 파싱 및 라벨링

**musinsa-redis-exporter**
- Redis 메트릭 수집
- Prometheus 포맷으로 Redis 성능 지표 제공
- 연결 상태, 메모리 사용량, 명령어 통계

**musinsa-redisinsight**
- Redis 데이터 시각화 및 관리 도구
- 키-값 데이터 조회 및 편집
- 실시간 모니터링 및 성능 분석

## API 엔드포인트

| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/categories | 전체 카테고리 조회 |
| GET | /api/categories/{id} | 특정 카테고리 조회 |
| POST | /api/categories | 카테고리 생성 |
| PUT | /api/categories/{id} | 카테고리 수정 |
| DELETE | /api/categories/{id} | 카테고리 삭제 |
| GET | /actuator/health | 애플리케이션 상태 확인 |
| GET | /actuator/prometheus | Prometheus 메트릭 |

## 디렉토리 구조

```
musinsa2/
├── src/main/java/com/yl/musinsa2/          # 애플리케이션 소스
├── monitoring/                             # 모니터링 설정
│   ├── prometheus/prometheus.yml           # Prometheus 설정
│   ├── grafana/provisioning/               # Grafana 자동 설정
│   └── loki/                              # Loki/Promtail 설정
├── logs/                                  # 애플리케이션 로그
├── docker-compose.yml                     # 전체 서비스 정의
└── Dockerfile                             # 애플리케이션 컨테이너 이미지
```
