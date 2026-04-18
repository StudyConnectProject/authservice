# 🧪 Guía de Testing del AuthService

## Inicio Rápido - Testing

### 1. Iniciar los Servicios

```bash
# Linux/Mac
./start.sh

# Windows
start.bat

# O directamente
docker-compose up -d

# Esperar 30-40 segundos para que se inicialice todo
```

### 2. Verificar que Todo Está Corriendo

```bash
# Ver estado de contenedores
docker-compose ps

# Ver logs del servicio
docker-compose logs -f authservice

# Ver logs de base de datos
docker-compose logs -f postgres
```

### 3. Testing con CURL

#### Health Check
```bash
curl -X GET http://localhost:8080/api/v1/auth/health
```

Response esperado:
```
"Auth Service is running"
```

#### Registro
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

Response esperado (201):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "student@example.com",
    "roles": ["STUDENT"]
  }
}
```

#### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "password123",
    "ip_address": "192.168.1.1"
  }'
```

#### Refresh Token
```bash
# Reemplazar TOKEN_AQUI con el refresh_token obtenido
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refresh_token": "TOKEN_AQUI",
    "ip_address": "192.168.1.1"
  }'
```

#### Logout
```bash
# Reemplazar USER_ID con el id del usuario
curl -X POST http://localhost:8080/api/v1/auth/logout/USER_ID
```

Response esperado (204): Sin contenido

### 4. Testing con Postman

1. **Importar Colección**
   - Abrir Postman
   - Click en "Import"
   - Seleccionar `Postman_Collection.json`

2. **Configurar Environment**
   - Variable `base_url`: `http://localhost:8080`
   - El access_token y refresh_token se guardan automáticamente

3. **Ejecutar Requests**
   - Register: Crear nuevo usuario
   - Login: Autenticar usuario existente
   - Refresh: Renovar token
   - Logout: Cerrar sesión
   - Health: Verificar estado

### 5. Acceder a la Base de Datos

```bash
# Entrar a psql
docker-compose exec postgres psql -U authservice_user -d authservice_db

# Queries útiles
SELECT * FROM users;
SELECT * FROM roles;
SELECT * FROM user_roles;
SELECT * FROM refresh_tokens;
```

### 6. Testing Automatizado (Unit Tests)

```bash
# Ejecutar tests
mvn test

# Con cobertura
mvn test jacoco:report

# Solo un test específico
mvn test -Dtest=AuthServiceTest
```

## Casos de Uso para Testing

### ✅ Caso 1: Registro y Login Exitoso

```
1. Registrar usuario: student@test.com / password123 / STUDENT
   → Recibir access_token y refresh_token

2. Usar access_token para futuras peticiones
   → Header: Authorization: Bearer {access_token}

3. Si token expira, usar refresh_token
   → Obtener nuevo access_token
```

### ✅ Caso 2: Validación de Inputs

```
1. Intentar registrar con email inválido
   → Error: "Email should be valid"

2. Intentar registrar con password corto
   → Error: "Password should have at least 6 characters"

3. Intentar registrar con rol no válido
   → Error: "Role not found"
```

### ✅ Caso 3: Seguridad

```
1. Registro: student@test.com / password123
2. Login con password incorrecto
   → Error: "Invalid password"

3. Login con email no registrado
   → Error: "User not found"

4. Usar token revocado después de logout
   → Error: "Invalid refresh token"
```

### ✅ Caso 4: Refresh Token

```
1. Login y obtener tokens
2. Esperar a que access_token expire (3600 segundos)
3. Usar refresh_token
   → Obtener nuevo access_token
   → Token anterior se revoca
```

## Troubleshooting de Testing

### Problema: "Connection refused"

```bash
# Verificar que PostgreSQL está running
docker-compose ps

# Verificar logs de PostgreSQL
docker-compose logs postgres

# Solución: Esperar 30-40 segundos
```

### Problema: "Email already registered"

```bash
# Usar un email diferente en cada test
# O limpiar la base de datos

docker-compose exec postgres psql -U authservice_user -d authservice_db
DELETE FROM refresh_tokens;
DELETE FROM user_roles;
DELETE FROM users;

# Salir de psql
\q
```

### Problema: "Invalid token"

```
Posibles causas:
1. Token expirado (verificar expires_at)
2. JWT_SECRET diferente entre cliente y servidor
3. Token hash incorrecto

Solución: Obtener nuevo token con login/refresh
```

### Problema: "Port 5432 already in use"

```bash
# Cambiar puerto en docker-compose.yml
# De: "5432:5432"
# A: "5433:5432"

docker-compose down
docker-compose up -d
```

## Monitoreo en Tiempo Real

```bash
# Ver todos los logs en tiempo real
docker-compose logs -f

# Solo logs de authservice
docker-compose logs -f authservice

# Ver estadísticas de contenedores
docker stats

# Ver requests HTTP (si usas nginx o similar)
docker-compose logs -f --tail=50
```

## Performance Testing

```bash
# Instalar Apache Bench (Linux/Mac)
ab -n 1000 -c 10 http://localhost:8080/api/v1/auth/health

# O con load testing tool
# httperf, vegeta, locust, k6
```

## Checklist de Testing

- [ ] Health check retorna "running"
- [ ] Registro con email válido funciona
- [ ] Registro con email existente falla
- [ ] Login con credenciales correctas funciona
- [ ] Login con password incorrecto falla
- [ ] Access token permite usar protegido
- [ ] Refresh token genera nuevo access_token
- [ ] Logout revoca refresh_token
- [ ] Token expirado no puede usarse
- [ ] Base de datos tiene datos correctos
- [ ] CORS funciona desde cliente web
- [ ] Errores retornan status code correcto
- [ ] Logs se registran correctamente

## Comandos Útiles de Docker

```bash
# Ver logs últimas 50 líneas
docker-compose logs --tail=50

# Ver logs con timestamps
docker-compose logs -t

# Rebuild sin cache
docker-compose build --no-cache

# Up en background
docker-compose up -d

# Down eliminando volúmenes
docker-compose down -v

# Exec comando en contenedor
docker-compose exec authservice ./mvnw test

# Restart servicio
docker-compose restart authservice
```

---

**Ready to test! 🚀**
