#!/bin/bash

# Script para parar el AuthService

echo "Deteniendo AuthService..."
docker-compose down -v
echo "✓ AuthService detenido"
