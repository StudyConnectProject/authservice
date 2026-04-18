# 📚 GUÍA COMPLETA DE ENDPOINTS - AuthService API

## 🚀 Quick Start

### Importar Colección en Postman

1. **Abre Postman**
2. **Click en File → Import** (o arrastra el archivo)
3. **Selecciona:** `Postman_AuthService_Collection.json`
4. **¡Listo!** Ya tienes todos los endpoints configurados

---

## 📋 ESTRUCTURA DE ENDPOINTS

### 1️⃣ **POST /api/auth/register** - Registrar Usuario

Registra un nuevo usuario en el sistema con sus datos básicos.

**URL:**
```
http://localhost:8080/api/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "juan@gmail.com",
  "password": "SecurePassword123",
  "role": "STUDENT"
}
```

**Roles disponibles:**
- `STUDENT` - Estudiante
- `TUTOR` - Tutor/Profesor

**✅ Respuesta esperada (201 Created):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "juan@gmail.com",
    "roles": ["STUDENT"]
  }
}
```

**⚙️ Detalles importantes:**
- `expires_in` = 3600000 ms = 1 hora
- El email debe ser único (no puede registrar dos usuarios con el mismo email)
- La contraseña debe tener al menos 6 caracteres
- Guarda los tokens para usarlos después

---

### 2️⃣ **POST /api/auth/login** - Iniciar Sesión

Autentica al usuario con email y contraseña existentes.

**URL:**
```
http://localhost:8080/api/auth/login
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "juan@gmail.com",
  "password": "SecurePassword123",
  "ip_address": "192.168.1.100"
}
```

**📌 Notas:**
- El `ip_address` es **opcional** (puedes omitirlo)
- La contraseña debe ser exactamente igual a la registrada

**✅ Respuesta esperada (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "juan@gmail.com",
    "roles": ["STUDENT"]
  }
}
```

**❌ Posibles errores:**
- `400 Bad Request` → Email o password incorrectos
- `401 Unauthorized` → Credenciales inválidas
- `404 Not Found` → Usuario no existe

---

### 3️⃣ **POST /api/auth/refresh** - Refrescar Token

Genera un nuevo access token usando un refresh token válido.

**URL:**
```
http://localhost:8080/api/auth/refresh
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "ip_address": "192.168.1.100"
}
```

**📌 Notas:**
- Reemplaza `"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."` con el `refresh_token` real que recibiste
- El `ip_address` es **opcional**
- El refresh token tiene validez de **24 horas**

**✅ Respuesta esperada (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... (NUEVO)",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... (NUEVO)",
  "token_type": "Bearer",
  "expires_in": 3600000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "juan@gmail.com",
    "roles": ["STUDENT"]
  }
}
```

**💡 Importante:** Los tokens se renuevan completamente. Usa los nuevos tokens de aquí en adelante.

**❌ Posibles errores:**
- `400 Bad Request` → Token refresh inválido o expirado
- `401 Unauthorized` → Token no reconocido

---

### 4️⃣ **POST /api/auth/validate-token** - Validar Token ✨ NUEVO

Verifica si un token JWT es válido, no expirado y correctamente firmado.

**URL:**
```
http://localhost:8080/api/auth/validate-token
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**📌 Notas:**
- Reemplaza con tu `access_token` real
- Útil para verificar que un token no ha expirado antes de usarlo

**✅ Respuesta esperada (200 OK):**
```json
{
  "is_valid": true,
  "is_expired": false,
  "message": "Token is valid",
  "user_id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "juan@gmail.com",
  "roles": ["STUDENT"],
  "expires_at": 3600000
}
```

**❌ Posibles errores:**
- `400 Bad Request` → Token inválido
- `401 Unauthorized` → Token expirado

**Casos de respuesta:**
- `is_valid: true, is_expired: false` → Token OK ✅
- `is_valid: false, is_expired: true` → Token expirado, usa refresh
- `is_valid: false, is_expired: false` → Token inválido (tampering)

---

### 5️⃣ **POST /api/auth/logout** - Cerrar Sesión

Cierra la sesión del usuario invalidando todos los refresh tokens.

**URL:**
```
http://localhost:8080/api/auth/logout/550e8400-e29b-41d4-a716-446655440000
```

**📌 Reemplaza:**
- `550e8400-e29b-41d4-a716-446655440000` con el `user_id` del usuario

**Headers:**
```
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**📌 Reemplaza:**
- `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` con tu `access_token`

**Body:**
```
(Vacío - no necesita body)
```

**✅ Respuesta esperada (204 No Content):**
```
(Sin contenido - solo headers)
```

**💡 Qué hace:**
- Invalida TODOS los refresh tokens del usuario
- El usuario debe hacer login de nuevo para obtener nuevos tokens
- Los access tokens ya emitidos siguen siendo válidos hasta que expiren

---

### 6️⃣ **GET /api/auth/health** - Health Check

Verifica que el servicio AuthService está funcionando correctamente.

**URL:**
```
http://localhost:8080/api/auth/health
```

**Headers:**
```
(Ninguno necesario)
```

**Body:**
```
(Vacío)
```

**✅ Respuesta esperada (200 OK):**
```
Auth Service is running
```

**Uso:** Es útil para verificar que el servicio está arriba antes de hacer otras llamadas.

---

## 🎯 FLUJO RECOMENDADO PARA PROBAR

### Seguir este orden:

1. **Health Check** → Verifica que el servicio está activo
2. **Register** → Crea un nuevo usuario → Obtén tokens
3. **Validate Token** → Verifica que el token es válido
4. **Refresh Token** → Refresca el token → Obtén nuevos tokens
5. **Login** → Inicia sesión como el mismo usuario → Obtén nuevos tokens
6. **Logout** → Cierra la sesión
7. **Health Check** → Verifica que el servicio sigue activo

---

## 📝 EJEMPLOS DE FLUJO COMPLETO

### Ejemplo 1: Registro e Inmediata Validación

```bash
# 1. Registrar usuario
POST /api/auth/register
Body: {email: "test@gmail.com", password: "Pass123", role: "STUDENT"}
↓ Respuesta: access_token, refresh_token

# 2. Validar token recibido
POST /api/auth/validate-token
Body: {access_token: "<el_token_recibido>"}
↓ Respuesta: {is_valid: true, user_id: "xxx", ...}
```

### Ejemplo 2: Ciclo de vida del Token

```bash
# 1. Login
POST /api/auth/login
Body: {email: "test@gmail.com", password: "Pass123"}
↓ Respuesta: access_token, refresh_token (expires in 3600000 ms = 1 hora)

# 2. Pasada 1 hora, el token expira
# Usar refresh token para obtener nuevo token

# 3. Refresh token
POST /api/auth/refresh
Body: {refresh_token: "<refresh_token_anterior>"}
↓ Respuesta: access_token (NUEVO), refresh_token (NUEVO)

# 4. Repetir paso 3 cada 24 horas (vida del refresh token)
```

### Ejemplo 3: Logout y Nueva Sesión

```bash
# 1. Logout (invalida refresh tokens)
POST /api/auth/logout/{user_id}
Authorization: Bearer <access_token>
↓ Respuesta: 204 No Content

# 2. Login de nuevo
POST /api/auth/login
Body: {email: "test@gmail.com", password: "Pass123"}
↓ Respuesta: access_token (NUEVO), refresh_token (NUEVO)
```

---

## 🔐 AUTENTICACIÓN EN POSTMAN

### Para endpoints que requieren Authorization:

1. Abre el endpoint en Postman
2. Tab **Authorization**
3. Selecciona **Bearer Token**
4. En el campo **Token**, pega tu `access_token`

Automáticamente se agregará el header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🧪 CASOS DE ERROR COMUNES

| Error | Causa | Solución |
|-------|-------|----------|
| `400 Bad Request` | Email/password inválidos | Verifica que el formato sea correcto |
| `401 Unauthorized` | Token expirado | Usa refresh token para obtener uno nuevo |
| `404 Not Found` | Usuario no existe | Primero registra un usuario |
| `409 Conflict` | Email ya registrado | Usa otro email |
| `500 Internal Server Error` | Error del servidor | Verifica los logs del contenedor |

---

## 🔄 VARIABLES DE ENTORNO (Opcional)

Para una experiencia mejor, usa variables en Postman:

1. **Click en Environment** (arriba a la derecha)
2. **Click en + New**
3. **Agrega estas variables:**

```
base_url = http://localhost:8080
email = test@gmail.com
password = SecurePassword123
access_token = (se llena cuando registras)
refresh_token = (se llena cuando registras)
user_id = (se llena cuando registras)
```

4. **Usalas en las URLs:**
```
{{base_url}}/api/auth/login
{{base_url}}/api/auth/logout/{{user_id}}
```

---

## 📊 INFORMACIÓN DEL SERVICIO

- **Puerto:** 8080
- **Base URL:** http://localhost:8080
- **Versión API:** /api/auth
- **Autenticación:** JWT (Bearer Token)
- **Token Expiration:** 3600000 ms (1 hora)
- **Refresh Token Expiration:** 86400000 ms (24 horas)

---

¡**Listo**! Ahora puedes probar todos los endpoints. 🚀
