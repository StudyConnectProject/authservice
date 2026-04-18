@echo off

REM Script para parar AuthService en Windows

echo Deteniendo AuthService...
docker-compose down -v
echo [OK] AuthService detenido
pause
