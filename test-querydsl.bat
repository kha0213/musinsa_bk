@echo off
echo ====================================
echo   QueryDSL Q클래스 확인 및 테스트
echo ====================================

echo.
echo 1. Q클래스 존재 확인...

if exist "src\main\generated\com\yl\musinsa2\entity\QCategory.java" (
    echo ✅ QCategory.java 존재
) else (
    echo ❌ QCategory.java 없음
    goto :generate
)

if exist "src\main\generated\com\yl\musinsa2\entity\QBaseEntity.java" (
    echo ✅ QBaseEntity.java 존재
) else (
    echo ❌ QBaseEntity.java 없음
    goto :generate
)

echo.
echo 2. Q클래스 내용 확인...
echo QCategory.java 첫 10줄:
type "src\main\generated\com\yl\musinsa2\entity\QCategory.java" | more +1 | head -10

echo.
echo 3. 컴파일 테스트...
call gradlew compileJava

if %ERRORLEVEL% EQU 0 (
    echo ✅ 컴파일 성공!
) else (
    echo ❌ 컴파일 실패
    goto :generate
)

echo.
echo 4. 테스트 실행...
call gradlew test

if %ERRORLEVEL% EQU 0 (
    echo ✅ 테스트 성공! QueryDSL이 정상 작동합니다.
) else (
    echo ❌ 테스트 실패
)

goto :end

:generate
echo.
echo Q클래스가 없거나 문제가 있습니다. 재생성합니다...
call gradlew clean
call gradlew compileJava
goto :end

:end
echo.
echo 완료!
pause
