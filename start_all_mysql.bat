@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ========================================================
echo   Enterprise Employee Management System - MySQL Mode
echo ========================================================
echo.

REM 0. Smart Environment Detection (Session Only)
call env_detect.bat

REM 1. Check Environment
call check_env.bat --auto-fix --no-pause
if %ERRORLEVEL% NEQ 0 (
    echo Environment check failed.
    pause
    exit /b 1
)

powershell -NoProfile -Command "$ok=Test-NetConnection -ComputerName 127.0.0.1 -Port 3306 -WarningAction SilentlyContinue; if($ok.TcpTestSucceeded){exit 0}else{exit 1}"
if %ERRORLEVEL% NEQ 0 (
    echo [WARN] MySQL service is unavailable on 3306.
    echo [WARN] Auto-switching to H2 mode for stable startup...
    call .\start_all.bat
    exit /b 0
)

call preflight.bat mysql

echo [1/2] Starting Backend with MySQL profile...
start "Backend Server (8080)-MySQL" cmd /c ".\run_backend_mysql.bat"

echo [2/2] Starting Frontend...
if exist ".\frontend\node_modules" (
    start "Frontend Server (5173)" cmd /c "cd frontend && npm run dev"
) else (
    start "Frontend Server (5173)" cmd /c "cd frontend && npm install && npm run dev"
)
start "Health Check" cmd /c "timeout /t 12 >nul && .\health_check.bat --open && pause"

echo.
echo Open browser: http://localhost:5173
echo Login: admin / 123456
echo.
pause
