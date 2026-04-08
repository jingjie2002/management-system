@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ========================================================
echo   Enterprise Employee Management System - One-Click Start
echo ========================================================
echo.

if not exist ".\mvnw.cmd" (
    echo [ERROR] mvnw.cmd not found in project root.
    echo [HINT] Please run this script in the project root directory.
    pause
    exit /b 1
)

if not exist ".\frontend\package.json" (
    echo [ERROR] frontend\package.json not found.
    echo [HINT] Please make sure the frontend folder exists.
    pause
    exit /b 1
)

where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found in PATH.
    echo [HINT] Please install JDK 17+ and ensure java command is available.
    pause
    exit /b 1
)

where node >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Node.js not found in PATH.
    echo [HINT] Please install Node.js 18+.
    pause
    exit /b 1
)

echo [1/3] Checking backend status on http://localhost:8080 ...
powershell -NoProfile -Command "try { $r=Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:8080/api/health' -TimeoutSec 2; if($r.StatusCode -eq 200){exit 0}else{exit 1} } catch { exit 1 }"
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Backend is already running. Skip starting a new backend process.
) else (
    echo [INFO] Starting backend Spring Boot...
    start "Backend Server (8080)" cmd /k "cd /d \"%~dp0\" && .\mvnw.cmd spring-boot:run"
)

echo [2/3] Waiting for backend health check...
powershell -NoProfile -Command "$ok=$false; for($i=0;$i -lt 60;$i++){ try { $r=Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:8080/api/health' -TimeoutSec 2; if($r.StatusCode -eq 200){$ok=$true; break} } catch {}; Start-Sleep -Seconds 1 }; if($ok){exit 0}else{exit 1}"
if %ERRORLEVEL% NEQ 0 (
    echo [WARN] Backend health check is still not ready.
    echo [WARN] Frontend will continue to start. Please check backend window logs.
)

echo [3/3] Starting frontend (Vue 3)...
powershell -NoProfile -Command "try { $r=Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:5173' -TimeoutSec 2; if($r.StatusCode -eq 200){exit 0}else{exit 1} } catch { exit 1 }"
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Frontend is already running. Skip starting a new frontend process.
) else (
    if exist ".\frontend\node_modules" (
        start "Frontend Server (5173)" cmd /k "cd /d \"%~dp0frontend\" && npm run dev"
    ) else (
        start "Frontend Server (5173)" cmd /k "cd /d \"%~dp0frontend\" && npm install && npm run dev"
    )
)

start "" "http://localhost:5173"

echo.
echo ========================================================
echo Startup command finished.
echo Frontend UI: http://localhost:5173
echo Backend API: http://localhost:8080/swagger-ui.html
echo Login: admin / 123456
echo ========================================================
pause
