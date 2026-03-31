setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ========================================================
echo   Enterprise Employee Management System - One-Click Start
echo ========================================================
echo.

REM 0. Smart Environment Detection (Session Only)
call env_detect.bat

REM 1. Check Environment
call check_env.bat --auto-fix --no-pause
if %ERRORLEVEL% NEQ 0 (
    echo Environment check failed. Please fix issues first.
    pause
    exit /b 1
)

call preflight.bat h2

echo.
echo [1/2] Starting Backend (Spring Boot)...
echo Please wait for the backend to initialize (approx 10-30 seconds)...
echo Do NOT close this window.
start "Backend Server (38889)" cmd /c ".\run_backend.bat"

echo.
echo [2/2] Starting Frontend (Vue 3)...
if exist ".\frontend\node_modules" (
    start "Frontend Server (5174)" cmd /c "cd frontend && npm run dev"
) else (
    start "Frontend Server (5174)" cmd /c "cd frontend && npm install && npm run dev"
)
start "Health Check" cmd /c "timeout /t 12 >nul && .\health_check.bat --open && pause"

echo.
echo ========================================================
echo System is starting up!
echo Backend API: http://localhost:38889/swagger-ui/index.html
echo Frontend UI: http://localhost:5174
echo.
echo Please wait a moment for both windows to be ready.
echo Then open your browser to access the Frontend UI.
echo.
echo Login: admin / 123456
echo ========================================================
pause
