# 📋 Estructura Completa del AuthService - StudyConnect

## 📁 Archivos Creados y Modificados

### 🔧 Configuración del Proyecto

- ✅ `pom.xml` - Actualizado con todas las dependencias necesarias
  - Spring Boot Web, JPA, PostgreSQL
  - Spring Security
  - JWT (JJWT)
  - Lombok, Validation

- ✅ `application.properties` - Configuración de la aplicación
  - Base de datos PostgreSQL
  - Configuración JPA/Hibernate
  - JWT tokens (expiration, secret)
  - Logging

### 📦 Entities (Modelos de Datos)

- ✅ `User.java` - Entidad usuario con soporte UUID
  - Email único
  - Password hash
  - Relación many-to-many con roles
  - Estado activo/inactivo

- ✅ `Role.java` - Entidad rol
  - UUID primary key
  - Nombres: STUDENT, TUTOR
  - Estado activo/inactivo

- ✅ `UserRole.java` - Tabla de unión usuario-rol
  - UUIDs de usuario y rol
  - Timestamp de asignación
  - Constraint unique

- ✅ `RefreshToken.java` - Entidad refresh token
  - User ID (FK)
  - Token hash SHA-256
  - Device info e IP address
  - Expiration y revoke flag
  - Timestamp de creación

### 💾 Repositories (Acceso a Datos)

- ✅ `UserRepository.java` - Interface JPA
  - findByEmail()
  - existsByEmail()

- ✅ `RoleRepository.java` - Interface JPA
  - findByName()

- ✅ `RefreshTokenRepository.java` - Interface JPA
  - findByTokenHashAndIsRevokedFalse()
  - deleteByUserId()

### 📤 DTOs (Data Transfer Objects)

- ✅ `AuthRequestDto.java` - Request para login
  - Email, password, ip_address

- ✅ `AuthResponseDto.java` - Response de autenticación
  - Access token, refresh token
  - Token type, expiration
  - User data (id, email, roles)

- ✅ `RegisterRequestDto.java` - Request para registro
  - Email, password, role

- ✅ `RefreshTokenRequestDto.java` - Request para refresh
  - Refresh token, ip_address

### 🔐 Servicios y Lógica

- ✅ `AuthService.java` (Interface)
  - register(), login(), refreshToken(), logout()

- ✅ `AuthServiseImpl.java` (Implementación)
  - Lógica completa de autenticación
  - Generación de JWT
  - Validación de credenciales
  - Manejo de refresh tokens

### 🌐 Controllers (Endpoints REST)

- ✅ `AuthController.java` - Controlador principal
  - POST /api/v1/auth/register - Registro
  - POST /api/v1/auth/login - Login
  - POST /api/v1/auth/refresh - Refresh token
  - POST /api/v1/auth/logout/{userId} - Logout
  - GET /api/v1/auth/health - Health check

### 🛠️ Utilidades

- ✅ `JwtUtil.java` - Utilidad para JWT
  - generateAccessToken()
  - generateRefreshToken()
  - validateToken()
  - getUserIdFromToken()
  - getEmailFromToken()
  - isTokenExpired()

- ✅ `HashUtil.java` - Utilidad para hash
  - hashPassword() - SHA-256
  - verifyPassword()
  - hashToken()

### ⚙️ Configuración

- ✅ `SecurityConfig.java` - Configuración de seguridad
  - CORS permitidos
  - PasswordEncoder

- ✅ `database.java` - Inicialización de BD
  - CommandLineRunner para crear roles por defecto
  - Health check de BD

### 🚨 Manejo de Excepciones

- ✅ `ErrorResponse.java` - DTO de error
  - Status, message, error, timestamp, path

- ✅ `GlobalExceptionHandler.java` - Handler global de excepciones
  - RuntimeException
  - MethodArgumentNotValidException
  - Exception genérica

### 🐳 Docker y Orquestación

- ✅ `Dockerfile` - Multi-stage Docker build
  - Maven builder stage
  - Runtime JRE stage
  - Health check
  - Puerto 8080

- ✅ `docker-compose.yml` - Orquestación de servicios
  - PostgreSQL 16 Alpine
  - AuthService Spring Boot
  - Volumen persistente para datos
  - Network personalizada
  - Health checks
  - Inicialización automática de BD

- ✅ `.dockerignore` - Archivos a ignorar en Docker

- ✅ `init.sql` - Script SQL de inicialización
  - Creación de tablas
  - Índices
  - Insersión de roles por defecto

### 📝 Documentación y Scripts

- ✅ `README_AUTH.md` - Documentación completa
  - Descripción del proyecto
  - Requisitos previos
  - Instrucciones de inicio
  - Documentación de endpoints
  - Schema de BD
  - Troubleshooting

- ✅ `Postman_Collection.json` - Colección para testing
  - Endpoints de autenticación
  - Tests automáticos
  - Variables de environment
  - Health check

- ✅ `start.sh` - Script bash para iniciar (Linux/Mac)
- ✅ `stop.sh` - Script bash para parar (Linux/Mac)
- ✅ `start.bat` - Script batch para iniciar (Windows)
- ✅ `stop.bat` - Script batch para parar (Windows)

- ✅ `.env.example` - Variables de entorno de ejemplo

### 📋 Archivos de Proyecto

- ✅ `pom.xml` - Maven build script
- ✅ `mvnw` / `mvnw.cmd` - Maven wrapper

## 🗄️ Schema de Base de Datos

### USERS
```
id (UUID)
email (VARCHAR 255 UNIQUE NOT NULL)
password_hash (VARCHAR 255)
is_active (BOOLEAN DEFAULT true)
created_at (TIMESTAMP)
```

### ROLES
```
id (UUID)
name (VARCHAR 50 UNIQUE) - STUDENT, TUTOR
is_active (BOOLEAN DEFAULT true)
created_at (TIMESTAMP)
```

### USER_ROLES
```
id (UUID)
user_id (UUID FK → users.id)
role_id (UUID FK → roles.id)
assigned_at (TIMESTAMP)
UNIQUE(user_id, role_id)
```

### REFRESH_TOKENS
```
id (UUID)
user_id (UUID FK → users.id)
token_hash (VARCHAR 255)
ip_address (VARCHAR 45)
is_revoked (BOOLEAN)
expires_at (TIMESTAMP)
created_at (TIMESTAMP)
```

## 🚀 Inicio Rápido

### Con Docker Compose (Recomendado)

```bash
# Linux/Mac
chmod +x start.sh
./start.sh

# Windows
start.bat

# O directamente
docker-compose up -d
```

### Desarrollo Local

```bash
mvn clean install
mvn spring-boot:run
```

## 📡 API Endpoints

### Health
```
GET /api/v1/auth/health
```

### Registro
```
POST /api/v1/auth/register
Body: {
  "email": "user@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

### Login
```
POST /api/v1/auth/login
Body: {
  "email": "user@example.com",
  "password": "password123",
  "ip_address": "192.168.1.1"
}
```

### Refresh Token
```
POST /api/v1/auth/refresh
Body: {
  "refresh_token": "token...",
  "ip_address": "192.168.1.1"
}
```

### Logout
```
POST /api/v1/auth/logout/{userId}
```

## 🔑 Variables de Entorno Importantes

```
JWT_SECRET=mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly12345
JWT_EXPIRATION=3600000             # 1 hora
JWT_REFRESH_EXPIRATION=86400000    # 24 horas
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/authservice_db
SPRING_DATASOURCE_USERNAME=authservice_user
SPRING_DATASOURCE_PASSWORD=authservice_password
```

## ✅ Funcionalidades Implementadas

- ✅ Autenticación con JWT
- ✅ Registro de usuarios
- ✅ Login con validación
- ✅ Refresh token con revocación
- ✅ Logout con limpieza de tokens
- ✅ Sistema de roles (STUDENT, TUTOR)
- ✅ Hash seguro de contraseñas
- ✅ Manejo de excepciones global
- ✅ Validación de inputs
- ✅ CORS configurado
- ✅ Health checks
- ✅ Dockerizado completamente
- ✅ Base de datos PostgreSQL
- ✅ Índices de performance
- ✅ Logging estructurado

## ⚠️ Notas Importantes

1. **JWT_SECRET**: Cambiar en producción a un valor seguro
2. **HTTPS**: Usar en producción
3. **Rate Limiting**: Considerar añadir future versions
4. **Base de Datos**: Los valores en docker-compose NO deben usarse en producción
5. **CORS**: Configurar según necesidades reales

## 📚 Dependencias Principales

- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- JJWT (JWT Library)
- Lombok
- Validation API

---

**Proyecto**: StudyConnect - AuthService  
**Version**: 0.0.1-SNAPSHOT  
**Java Version**: 17  
**Status**: ✅ Listo para usar
