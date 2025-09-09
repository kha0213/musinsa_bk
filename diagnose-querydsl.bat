@echo off
echo ====================================
echo   QueryDSL 진단 및 Q클래스 생성
echo ====================================

echo.
echo 1. Java 버전 확인...
java -version

echo.
echo 2. Gradle 버전 확인...
call gradlew --version

echo.
echo 3. 프로젝트 완전 정리...
call gradlew clean

echo.
echo 4. 기존 generated 폴더 삭제...
if exist "src\main\generated" (
    rmdir /s /q "src\main\generated"
    echo generated 폴더 삭제 완료
)

echo.
echo 5. 의존성 확인...
call gradlew dependencies --configuration annotationProcessor

echo.
echo 6. 컴파일 실행 (상세 정보 포함)...
call gradlew compileJava --info --stacktrace

echo.
echo 7. 생성 결과 확인...
if exist "src\main\generated" (
    echo ✅ generated 폴더 생성됨!
    echo.
    echo 생성된 Q클래스들:
    if exist "src\main\generated\com\yl\musinsa2\entity\QCategory.java" (
        echo ✅ QCategory.java
    ) else (
        echo ❌ QCategory.java 없음
    )
    
    if exist "src\main\generated\com\yl\musinsa2\entity\QBaseEntity.java" (
        echo ✅ QBaseEntity.java
    ) else (
        echo ❌ QBaseEntity.java 없음
    )
    
    echo.
    echo 📁 전체 파일 목록:
    dir "src\main\generated" /s /b
    
) else (
    echo ❌ generated 폴더가 생성되지 않았습니다!
    echo.
    echo 🔍 문제 진단을 위해 build\tmp 폴더 확인...
    if exist "build\tmp" (
        dir "build\tmp" /s /b
    )
)

echo.
echo 8. 테스트 컴파일...
call gradlew compileTestJava

echo.
echo 진단 완료!
pause
