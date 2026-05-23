@echo off
chcp 65001 > nul
REM ─────────────────────────────────────────────
REM  অক্ষর কম্পাইলার - Windows Build Script
REM ─────────────────────────────────────────────

echo ══════════════════════════════════════
echo    অক্ষর কম্পাইলার বিল্ড স্ক্রিপ্ট
echo ══════════════════════════════════════

REM Create output directory
if not exist "out" mkdir out

REM Compile Java sources
echo.
echo Java সোর্স কম্পাইল করা হচ্ছে...
javac -encoding UTF-8 -d out src\compiler\*.java
if errorlevel 1 (
    echo Java কম্পাইল ব্যর্থ হয়েছে!
    pause
    exit /b 1
)
echo Java কম্পাইল সফল

REM Run compiler on argument
if "%1"=="" (
    echo.
    echo ব্যবহার: build.bat ফাইল.akkhor
    echo উদাহরণ: build.bat examples\হ্যালো.akkhor
) else (
    echo.
    echo ফাইল কম্পাইল করা হচ্ছে: %1
    java -cp out compiler.Main %1
)

pause
