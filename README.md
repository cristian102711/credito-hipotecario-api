# 🏦 Crédito Hipotecario API

API REST para gestión de propiedades y solicitudes de crédito hipotecario, desarrollada con Spring Boot 3 y arquitectura enterprise.

## Stack Tecnológico

- **Java 21** + **Spring Boot 4.x**
- **Spring Security 6** + **JWT** (autenticación stateless)
- **Spring Data JPA** + **Hibernate** (ORM)
- **PostgreSQL 15** (base de datos)
- **Flyway** (migraciones de base de datos)
- **Docker** + **Docker Compose** (containerización)
- **Swagger / OpenAPI 3** (documentación interactiva)
- **JUnit 5** + **Mockito** (tests unitarios)
- **Spring Actuator** (monitoreo)

## Módulos

### Autenticación
- Registro y login con JWT
- Refresh tokens
- Roles: `ADMIN`, `EJECUTIVO`, `CLIENTE`

### Propiedades
- CRUD completo con paginación
- Filtros avanzados: estado, tipo, comuna, precio, dormitorios
- Estados: `DISPONIBLE`, `RESERVADA`, `VENDIDA`
- Tipos: `CASA`, `DEPARTAMENTO`, `OFICINA`, `LOCAL_COMERCIAL`, `TERRENO`

### Solicitudes de Crédito
- Flujo de estados: `BORRADOR → ENVIADA → EN_REVISION → APROBADA / RECHAZADA`
- Cálculo automático de dividendo mensual y CAE
- Tasa de interés anual: 4.89%
- Plazo: 5 a 30 años

### Reportería
- Dashboard con estadísticas generales
- Exportación a PDF y Excel
- Resumen por estado y período

### Notificaciones
- Emails automáticos por cambio de estado
- Plantillas HTML profesionales

## Requisitos

- Java 21+
- Maven 3.9+
- Docker Desktop

## Levantar con Docker Compose

```bash
docker-compose up -d
```

Esto levanta PostgreSQL y la aplicación juntos. La app queda disponible en `http://localhost:8080`.

## Levantar en modo desarrollo

**1. Levantar PostgreSQL:**
```bash
docker run --name postgres-hipotecario \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=creditohipotecario \
  -p 5432:5432 -d postgres:15
```

**2. Ejecutar la aplicación:**
```bash
mvn spring-boot:run
```

##  Documentación

Con la app corriendo, accede a Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

Para autenticarte en Swagger:
1. Llama a `POST /api/auth/login`
2. Copia el `accessToken`
3. Haz click en **Authorize** 🔒
4. Pega el token y confirma

## 🔑 Endpoints principales

### Auth
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Iniciar sesión |

### Propiedades
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/propiedades` | Listar con paginación |
| GET | `/api/propiedades/filtrar` | Filtros avanzados |
| POST | `/api/propiedades` | Crear propiedad |
| PUT | `/api/propiedades/{id}` | Actualizar |
| PATCH | `/api/propiedades/{id}/estado` | Cambiar estado |
| DELETE | `/api/propiedades/{id}` | Eliminar |

### Solicitudes de Crédito
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/solicitudes` | Crear solicitud |
| GET | `/api/solicitudes/mis-solicitudes` | Mis solicitudes |
| GET | `/api/solicitudes` | Todas (ADMIN/EJECUTIVO) |
| POST | `/api/solicitudes/{id}/enviar` | Enviar solicitud |
| PATCH | `/api/solicitudes/{id}/estado` | Cambiar estado |

### Reportería
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/dashboard/estadisticas` | Estadísticas generales |
| GET | `/api/export/pdf` | Exportar PDF |
| GET | `/api/export/excel` | Exportar Excel |

## Tests

```bash
mvn test
```

7 tests unitarios cubriendo AuthService (register, email duplicado) y SolicitudCreditoService (cálculo CAE, dividendo, validaciones de estado).

##  Monitoreo

```
http://localhost:8080/actuator/health
```

## 👤 Autor

**Cristian Carlos Velasquez Cornejo**  
Analista Programador Fullstack  
[github.com/cristian102711](https://github.com/cristian102711)