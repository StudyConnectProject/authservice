#!/bin/bash

# Script para iniciar el AuthService con Docker Compose

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║           StudyConnect - AuthService Initialization             ║"
echo "╚════════════════════════════════════════════════════════════════╝"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar si Docker y Docker Compose están instalados
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker no está instalado${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}✗ Docker Compose no está instalado${NC}"
    exit 1
fi

echo -e "${YELLOW}→ Iniciando servicios...${NC}"
docker-compose up -d

echo -e "${YELLOW}→ Esperando a que la base de datos esté lista...${NC}"
sleep 5

echo -e "${YELLOW}→ Verificando estado de los servicios...${NC}"
docker-compose ps

echo ""
echo -e "${GREEN}✓ ¡AuthService iniciado exitosamente!${NC}"
echo ""
echo "═════════════════════════════════════════════════════════════════"
echo "📋 Información del Servicio:"
echo "═════════════════════════════════════════════════════════════════"
echo -e "API Base URL:     ${GREEN}http://localhost:8080${NC}"
echo -e "Documentación:    ${GREEN}http://localhost:8080/api/v1/auth/health${NC}"
echo -e "Base de Datos:    ${GREEN}postgres://authservice_user@localhost:5432/authservice_db${NC}"
echo ""
echo "═════════════════════════════════════════════════════════════════"
echo "🔗 Endpoints principales:"
echo "═════════════════════════════════════════════════════════════════"
echo "  POST   /api/v1/auth/register    - Registrar nuevo usuario"
echo "  POST   /api/v1/auth/login       - Login"
echo "  POST   /api/v1/auth/refresh     - Renovar token"
echo "  POST   /api/v1/auth/logout/{id} - Logout"
echo "  GET    /api/v1/auth/health      - Health check"
echo ""
echo "═════════════════════════════════════════════════════════════════"
echo "📝 Comandos útiles:"
echo "═════════════════════════════════════════════════════════════════"
echo "  Ver logs:        docker-compose logs -f authservice"
echo "  Parar:           docker-compose stop"
echo "  Eliminar:        docker-compose down -v"
echo "  Acceder a BD:    docker-compose exec postgres psql -U authservice_user -d authservice_db"
echo ""
