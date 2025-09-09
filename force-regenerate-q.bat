@echo off
echo ====================================
echo   QueryDSL Q파일 강제 재생성
echo ====================================

echo.
echo 1. Gradle 데몬 종료...
call gradlew --stop

echo.
echo 2. 기존 빌드 산출물 모두 삭제...
call gradlew clean

echo.
echo 3. generated 폴더 수동 삭제...
if exist "src\main\generated" (
    echo 기존 generated 폴더 삭제 중...
    rmdir /s /q "src\main\generated"
    echo 삭제 완료!
)

echo.
echo 4. build 폴더도 삭제...
if exist "build" (
    echo build 폴더 삭제 중...
    rmdir /s /q "build"
    echo 삭제 완료!
)

echo.
echo 5. 의존성 다운로드...
call gradlew build --refresh-dependencies

echo.
echo 6. 컴파일 실행 (Q클래스 생성)...
call gradlew compileJava --info

echo.
echo 7. 결과 확인...
if exist "src\main\generated" (
    echo ✅ generated 폴더 생성됨!
    echo.
    echo 📁 생성된 파일들:
    dir "src\main\generated" /s /b
) else (
    echo ❌ generated 폴더가 생성되지 않았습니다.
    echo.
    echo 문제 해결을 위해 다음을 시도해보세요:
    echo 1. Java 버전 확인: java -version
    echo 2. Gradle 버전 확인: gradlew --version
    echo 3. 의존성 확인: gradlew dependencies
)

echo.
echo 완료!
pause
