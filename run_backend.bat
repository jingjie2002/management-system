@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"
call .\env_detect.bat
if "%JAVA_HOME%"=="" (
    for /f "delims=" %%i in ('where javac 2^>nul') do (
        set "JAVAC_PATH=%%i"
        goto :found_javac
    )
)
:found_javac
if defined JAVAC_PATH (
    for %%i in ("!JAVAC_PATH!") do set "JAVA_HOME=%%~dpi.."
)
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found in PATH. Please run check_env.bat first.
    pause
    exit /b 1
)
if not exist ".\mvnw.cmd" (
    echo [ERROR] mvnw.cmd not found in project root.
    pause
    exit /b 1
)

:run_loop
echo Starting backend (Spring Boot)...
call .\mvnw.cmd spring-boot:run
if %ERRORLEVEL% NEQ 0 (
    echo Backend exited with error %ERRORLEVEL%.
) else (
    echo Backend stopped.
)
echo Restarting in 5 seconds (Ctrl+C to cancel)...
timeout /t 5 /nobreak >nul
goto run_loop
