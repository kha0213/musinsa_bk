# QueryDSL Q클래스 생성 문제 해결 가이드

## 🔧 Q클래스가 생성되지 않을 때 해결 방법

### 1단계: 현재 상태 확인
```bash
# Windows 사용자
final-verification.bat

# 또는 수동 확인
dir src\main\generated\com\yl\musinsa2\entity
```

### 2단계: 강제 재생성
```bash
# Windows 사용자  
force-regenerate-q.bat

# 또는 수동 실행
gradlew clean
gradlew compileJava --info
```

### 3단계: 문제 진단
```bash
# 의존성 확인
gradlew dependencies --configuration annotationProcessor

# 컴파일 로그 확인
gradlew compileJava --info --stacktrace
```

## 📁 Q클래스 위치
정상적으로 생성되면 다음 위치에 Q클래스들이 생성됩니다:
```
src/main/generated/
└── com/yl/musinsa2/entity/
    ├── QCategory.java
    └── QBaseEntity.java
```

## 🛠 수동 해결 방법

### 방법 1: Gradle 설정 확인
build.gradle에서 다음 설정이 있는지 확인:
```gradle
// QueryDSL
implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
implementation 'com.querydsl:querydsl-core:5.0.0'
annotationProcessor 'com.querydsl:querydsl-apt:5.0.0:jakarta'
annotationProcessor 'jakarta.annotation:jakarta.annotation-api'
annotationProcessor 'jakarta.persistence:jakarta.persistence-api'

// QueryDSL 설정
def generated = 'src/main/generated'
sourceSets {
    main.java.srcDirs += [generated]
}
tasks.withType(JavaCompile) {
    options.generatedSourceOutputDirectory = file(generated)
}
clean {
    delete file(generated)
}
```

### 방법 2: IDE 설정
IntelliJ IDEA 사용 시:
1. File → Project Structure → Modules
2. Sources 탭에서 `src/main/generated` 폴더를 Generated Sources Root로 마크
3. Gradle 재동기화: Gradle → Reload Gradle Project

### 방법 3: 캐시 정리
```bash
# Gradle 데몬 종료
gradlew --stop

# 캐시 정리 (선택사항)
# Windows: %USERPROFILE%\.gradle\caches 폴더 삭제
# 또는 프로젝트 .gradle 폴더 삭제

# 재시작
gradlew clean compileJava
```

## ✅ 검증 방법

### 1. Q클래스 존재 확인
```bash
# QCategory.java 확인
type src\main\generated\com\yl\musinsa2\entity\QCategory.java | more

# QBaseEntity.java 확인  
type src\main\generated\com\yl\musinsa2\entity\QBaseEntity.java | more
```

### 2. 컴파일 테스트
```bash
gradlew compileJava
```

### 3. QueryDSL 동작 테스트
```bash
gradlew test --tests "*QueryDslGenerationTest*"
```

## 🆘 여전히 문제가 있다면

1. **Java 버전 확인**: Java 21 사용 중인지 확인
2. **Gradle 버전 확인**: Gradle 8.x 사용 중인지 확인  
3. **Entity 어노테이션 확인**: @Entity, @Id 등이 제대로 있는지 확인
4. **Lombok 충돌**: Lombok과 QueryDSL annotation processor 순서 문제일 수 있음

### 최종 해결책: 수동 Q클래스 생성
정말 안 될 경우, 기존 Q클래스를 복사해서 사용하거나 문의 주세요.

## 📞 지원
문제가 계속되면 다음 정보와 함께 문의해주세요:
- `gradlew --version` 출력
- `java -version` 출력  
- `gradlew compileJava --stacktrace` 로그
