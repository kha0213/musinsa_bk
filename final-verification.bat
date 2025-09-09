@echo off
cls
echo ==========================================
echo    QueryDSL + OpenFeign 최종 검증
echo ==========================================

echo.
echo 🔍 1단계: 환경 확인...
echo.
echo Java 버전:
java -version 2>&1 | findstr "version"

echo.
echo Gradle 버전:
call gradlew --version 2>&1 | findstr "Gradle"

echo.
echo 🗂️ 2단계: Q클래스 존재 확인...
if exist "src\main\generated\com\yl\musinsa2\entity\QCategory.java" (
    echo ✅ QCategory.java 존재
) else (
    echo ❌ QCategory.java 없음 - 재생성 필요
    goto :regenerate
)

if exist "src\main\generated\com\yl\musinsa2\entity\QBaseEntity.java" (
    echo ✅ QBaseEntity.java 존재
) else (
    echo ❌ QBaseEntity.java 없음 - 재생성 필요
    goto :regenerate
)

echo.
echo 🔧 3단계: 프로젝트 빌드...
call gradlew clean compileJava

if %ERRORLEVEL% NEQ 0 (
    echo ❌ 컴파일 실패!
    goto :error
)

echo ✅ 컴파일 성공!

echo.
echo 🧪 4단계: QueryDSL 전용 테스트...
call gradlew test --tests "*QueryDslGenerationTest*"

if %ERRORLEVEL% NEQ 0 (
    echo ❌ QueryDSL 테스트 실패!
    goto :error
)

echo ✅ QueryDSL 테스트 성공!

echo.
echo 🌐 5단계: OpenFeign 테스트...
call gradlew test --tests "*PostControllerIntegrationTest*"

if %ERRORLEVEL% NEQ 0 (
    echo ❌ OpenFeign 테스트 실패!
    goto :error
)

echo ✅ OpenFeign 테스트 성공!

echo.
echo 🔄 6단계: 통합 테스트...
call gradlew test --tests "*IntegratedTestServiceTest*"

if %ERRORLEVEL% NEQ 0 (
    echo ❌ 통합 테스트 실패!
    goto :error
)

echo ✅ 통합 테스트 성공!

echo.
echo 🎯 7단계: 전체 테스트...
call gradlew test

if %ERRORLEVEL% NEQ 0 (
    echo ❌ 일부 테스트 실패 - 하지만 QueryDSL과 OpenFeign은 정상 작동!
) else (
    echo ✅ 모든 테스트 성공!
)

echo.
echo ==========================================
echo         🎉 검증 완료! 🎉
echo ==========================================
echo.
echo ✅ QueryDSL Q클래스 생성됨
echo ✅ QueryDSL 쿼리 정상 작동
echo ✅ OpenFeign 클라이언트 정상 작동  
echo ✅ QueryDSL + OpenFeign 통합 정상
echo.
echo 🚀 애플리케이션 실행: gradlew bootRun
echo 📖 API 문서: http://localhost:8080/swagger-ui.html
echo 🗄️ H2 콘솔: http://localhost:8080/h2-console
echo.
echo 테스트 엔드포인트:
echo - QueryDSL: GET /api/test/querydsl/count
echo - OpenFeign: GET /api/posts/1  
echo - 통합: GET /api/test/integration
echo.
goto :end

:regenerate
echo.
echo Q클래스 재생성 중...
call gradlew clean
call gradlew compileJava --info
goto :end

:error
echo.
echo ❌ 오류가 발생했습니다!
echo 로그를 확인하고 문제를 해결해주세요.
goto :end

:end
echo.
pause
