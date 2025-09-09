@echo off
echo ====================================
echo   MUSINSA2 프로젝트 빌드 및 실행
echo ====================================

echo.
echo 1. 프로젝트 클린...
call gradlew clean

echo.
echo 2. QueryDSL Q클래스 생성...
call gradlew compileJava

echo.
echo 3. 테스트 실행...
call gradlew test

echo.
echo 4. 빌드 완료!
echo.
echo 실행하려면 다음 명령어를 사용하세요:
echo gradlew bootRun
echo.
echo 또는 다음 URL에서 API 문서를 확인하세요:
echo http://localhost:8080/swagger-ui.html
echo.

pause
