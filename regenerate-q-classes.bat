@echo off
echo ====================================
echo   QueryDSL Q파일 재생성 스크립트
echo ====================================

echo.
echo 1. 프로젝트 클린...
call gradlew clean

echo.
echo 2. generated 폴더 확인 및 삭제...
if exist "src\main\generated" (
    echo generated 폴더가 존재합니다. 삭제합니다...
    rmdir /s /q "src\main\generated"
)

echo.
echo 3. QueryDSL Q클래스 생성...
call gradlew compileJava

echo.
echo 4. Q클래스 생성 확인...
if exist "src\main\generated\com\yl\musinsa2\entity\QCategory.java" (
    echo ✅ QCategory.java 생성 성공!
) else (
    echo ❌ QCategory.java 생성 실패
)

if exist "src\main\generated\com\yl\musinsa2\entity\QBaseEntity.java" (
    echo ✅ QBaseEntity.java 생성 성공!
) else (
    echo ❌ QBaseEntity.java 생성 실패
)

echo.
echo 5. generated 폴더 구조 확인...
if exist "src\main\generated" (
    echo generated 폴더 내용:
    tree /f "src\main\generated"
) else (
    echo generated 폴더가 생성되지 않았습니다.
)

echo.
echo 완료!
pause
