@echo off

REM Script para iniciar AuthService en Windows

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║           StudyConnect - AuthService Initialization             ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Verificar si Docker está instalado
docker -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo X Docker no está instalado
    pause
    exit /b 1
)

REM Verificar si Docker Compose está instalado
docker-compose -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo X Docker Compose no está instalado
    pause
    exit /b 1
)

echo ... Iniciando servicios...
docker-compose up -d

echo ... Esperando a que la base de datos esté lista...
timeout /t 5 /nobreak

echo ... Verificando estado de los servicios...
docker-compose ps

echo.
echo [OK] ¡AuthService iniciado exitosamente!
echo.
echo ═════════════════════════════════════════════════════════════════
echo Información del Servicio:
echo ═════════════════════════════════════════════════════════════════
echo API Base URL:     http://localhost:8080
echo Documentación:    http://localhost:8080/api/v1/auth/health
echo Base de Datos:    postgres://authservice_user@localhost:5432/authservice_db
echo.
echo ═════════════════════════════════════════════════════════════════
echo Endpoints principales:
echo ═════════════════════════════════════════════════════════════════
echo  POST   /api/v1/auth/register    - Registrar nuevo usuario
echo  POST   /api/v1/auth/login       - Login
echo  POST   /api/v1/auth/refresh     - Renovar token
echo  POST   /api/v1/auth/logout/{id} - Logout
echo  GET    /api/v1/auth/health      - Health check
echo.
echo ═════════════════════════════════════════════════════════════════
echo Comandos útiles:
echo ═════════════════════════════════════════════════════════════════
echo  Ver logs:        docker-compose logs -f authservice
echo  Parar:           docker-compose stop
echo  Eliminar:        docker-compose down -v
echo.
pause
