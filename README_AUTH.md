# AuthService - StudyConnect Microservicio

Microservicio de autenticación independiente para el proyecto StudyConnect.

## Descripción

AuthService es un microservicio Spring Boot que proporciona:
- ✅ Registro de usuarios
- ✅ Login con JWT
- ✅ Refresh tokens con revocación
- ✅ Gestión de roles (STUDENT, TUTOR)
- ✅ Base de datos PostgreSQL
- ✅ Completamente dockerizado

## Características Principales

### Autenticación
- JWT (JSON Web Tokens) para acceso seguro
- Refresh tokens con expiración de 24 horas
- Hashing SHA-256 de contraseñas y tokens
- Revocación de tokens por usuario

### Base de Datos
- PostgreSQL 16
- Esquema: USERS, ROLES, USER_ROLES, REFRESH_TOKENS
- Índices para mejor performance

### Seguridad
- Spring Security integrado
- Validación de entrada en DTOs
- Control de acceso por roles

## Requisitos Previos

- Docker
- Docker Compose
- (Opcional) Java 17 y Maven para desarrollo local

## Inicio Rápido

### 1. Ejecutar con Docker Compose

```bash
# Clonar o descargar el proyecto
cd authservice

# Iniciar los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f authservice
```

El servicio estará disponible en: `http://localhost:8080`

### 2. Desarrollo Local

```bash
# Compilar
mvn clean package

# Ejecutar
mvn spring-boot:run
```

## API Endpoints

### 1. Registro
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123",
  "role": "STUDENT"  # STUDENT o TUTOR
}

Response 201:
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "user": {
    "id": "uuid",
    "email": "usuario@example.com",
    "roles": ["STUDENT"]
  }
}
```

### 2. Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123",
  "ip_address": "192.168.1.1"
}

Response 200: [igual que registro]
```

### 3. Refresh Token
```bash
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
  "ip_address": "192.168.1.1"
}

Response 200: [nuevo access_token]
```

### 4. Logout
```bash
POST /api/v1/auth/logout/{userId}

Response 204 No Content
```

### 5. Health Check
```bash
GET /api/v1/auth/health

Response 200:
"Auth Service is running"
```

## Configuración

### Variables de Entorno

```
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/authservice_db
SPRING_DATASOURCE_USERNAME=authservice_user
SPRING_DATASOURCE_PASSWORD=authservice_password
JWT_SECRET=mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly12345
JWT_EXPIRATION=3600000          # 1 hora en milliseconds
JWT_REFRESH_EXPIRATION=86400000 # 24 horas en milliseconds
```

## Estructura del Proyecto

```
authservice/
├── src/
│   ├── main/
│   │   ├── java/com/studyconnetct/authservice/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── service/         # Business Logic
│   │   │   ├── repository/      # Data Access
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── util/            # Utilidades (JWT, Hash)
│   │   │   ├── config/          # Configuración
│   │   │   └── AuthserviceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── init.sql         # Script de inicialización DB
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Base de Datos - Schema

### USERS
```sql
id          UUID PRIMARY KEY
email       VARCHAR(255) UNIQUE NOT NULL
password_hash VARCHAR(255) NOT NULL
is_active   BOOLEAN DEFAULT true
created_at  TIMESTAMP DEFAULT NOW()
```

### ROLES
```sql
id          UUID PRIMARY KEY
name        VARCHAR(50) UNIQUE NOT NULL (STUDENT, TUTOR)
is_active   BOOLEAN DEFAULT true
created_at  TIMESTAMP DEFAULT NOW()
```

### USER_ROLES
```sql
id          UUID PRIMARY KEY
user_id     UUID NOT NULL REFERENCES USERS(id) ON DELETE CASCADE
role_id     UUID NOT NULL REFERENCES ROLES(id) ON DELETE CASCADE
assigned_at TIMESTAMP DEFAULT NOW()
UNIQUE(user_id, role_id)
```

### REFRESH_TOKENS
```sql
id          UUID PRIMARY KEY
user_id     UUID NOT NULL REFERENCES USERS(id) ON DELETE CASCADE
token_hash  VARCHAR(255) NOT NULL
ip_address  VARCHAR(45)
is_revoked  BOOLEAN DEFAULT false
expires_at  TIMESTAMP NOT NULL
created_at  TIMESTAMP DEFAULT NOW()
```

## Comandos Docker Útiles

```bash
# Ver estado de contenedores
docker-compose ps

# Ver logs del servicio
docker-compose logs -f authservice

# Ver logs de la BD
docker-compose logs -f postgres

# Parar servicios
docker-compose stop

# Eliminar contenedores y volúmenes
docker-compose down -v

# Reconstruir imagen
docker-compose build --no-cache

# Acceder a la BD
docker-compose exec postgres psql -U authservice_user -d authservice_db
```

## Testing

```bash
# Ejecutar pruebas unitarias
mvn test

# Con cobertura
mvn test jacoco:report
```

## Troubleshooting

### Conexión rechazada a PostgreSQL
```bash
# Verificar que la BD está lista
docker-compose logs postgres

# Esperar a que el healthcheck pase (2-3 minutos)
docker-compose ps
```

### Puerto 5432 ya en uso
```bash
# Cambiar en docker-compose.yml
ports:
  - "5433:5432"  # Usar puerto diferente
```

### Error de JWT inválido
- Verificar que JWT_SECRET sea igual en client y server
- Verificar que el token no esté expirado

## Seguridad

- ⚠️ **IMPORTANTE**: Cambiar `JWT_SECRET` en producción
- ⚠️ Usar HTTPS en producción
- ⚠️ Implementar rate limiting
- ⚠️ Validar CORS según necesidades

## Roadmap Futuro

- [ ] OAuth2 / Google Sign-In
- [ ] Two-Factor Authentication
- [ ] Session Management
- [ ] Integration con API Gateway
- [ ] Métrica y monitoring
- [ ] Rate Limiting
- [ ] Audit Logging

## Contribución

Crear issues o PRs según sea necesario.

## Licencia

Proyecto interno StudyConnect.
