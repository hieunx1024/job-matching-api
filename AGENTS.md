# AGENTS.md - AI Agent Guide for JobHunter Project

## Overview
JobHunter is a full-stack recruitment platform with a Spring Boot 3 backend (Java 17) and React 19 frontend. It implements a multi-role system (Admin, HR, Candidate) with role-based access control, JWT authentication, and integrated payment processing.

---

## Architecture & Data Flow

### Backend Stack
- **Framework**: Spring Boot 3.2.4
- **Database**: PostgreSQL (production) / MySQL (development)
- **Build Tool**: Gradle (Kotlin DSL) with Wrapper
- **Security**: Spring Security 6.x with JWT + HttpOnly Cookies
- **ORM**: Spring Data JPA with custom specifications for filtering

### Frontend Stack
- **Framework**: React 19 with Vite
- **Styling**: Tailwind CSS v4 + Ant Design v6
- **State Management**: React Context (AuthContext)
- **API Client**: Axios with request/response interceptors
- **Form Validation**: React Hook Form + Yup

### Deployment
- **Backend**: Docker (multi-stage build), deployed on Render
- **Frontend**: Built with Vite, no separate deployment file present
- **Database**: Managed PostgreSQL on Render

---

## Layer Architecture Pattern

Every feature follows the strict **Controller → Service → Repository → Entity** pattern:

```
AuthController.java     ← HTTP entry point, parameter validation, @ApiMessage
    ↓
UserService.java        ← Business logic, transactions, cross-entity concerns
    ↓
UserRepository.java     ← JPA Spring Data (QueryMethods + JobSpecification)
    ↓
User.java (Entity)      ← @Entity with JPA annotations, Lombok @Getter/@Setter
```

**Key Rule**: Never bypass this pattern. Services handle business logic; components never call repositories directly.

---

## Critical Patterns & Conventions

### 1. Response Format & Error Handling
**All API responses are wrapped** in `RestResponse<T>` via `FormatRestResponse` (ResponseBodyAdvice):

```java
// Success response (automatic wrapping)
{
  "statusCode": 200,
  "message": "User created successfully",  // From @ApiMessage annotation
  "data": { ... }
}

// Error response (GlobalException handler)
{
  "statusCode": 400,
  "error": "Invalid request data",
  "message": "email is required"
}
```

**Convention**: Add `@ApiMessage("description")` to every controller method that returns data.

### 2. Exception Handling
Custom exceptions in `util/error/`:
- `IdInvalidException` → 400 Bad Request
- `PermissionException` → 403 Forbidden
- `PostLimitExceededException` → 402 Payment Required
- `StorageException` → 400 Bad Request

Always throw specific exceptions; `GlobalException` catches and wraps them.

### 3. Authentication & Authorization
**JWT Strategy**:
- Access Token: Stored in frontend localStorage, sent via `Authorization: Bearer <token>`
- Refresh Token: HttpOnly Cookie (secure, automatic)
- Token includes user ID, email, permissions encoded as authorities

**Permission Check**:
- `PermissionInterceptor` validates JWT issue time vs. password change time
- Method-level security via Spring AOP (though permission-based is preferred)

**Frontend Pattern** (axiosClient.js):
- Request interceptor adds token to headers
- Response interceptor detects 401, calls refresh endpoint, retries request
- Failed refresh → clear storage and redirect to /login

### 4. Request/Response DTOs
Located in `domain/request/` and `domain/response/`:
- **Request DTOs**: Typically in request/ folder (e.g., `ReqLoginDTO.java`)
- **Response DTOs**: Wrap sensitive data (no passwords, no refresh tokens). Use `@JsonIgnore` for fields to exclude.
- **Nested DTO Pattern**: `ResLoginDTO` contains inner class `UserInsideToken` for token payload

**Example**:
```java
@Data
public class ResLoginDTO {
    private UserInsideToken user;
    private String access_token;
    
    @Data
    public static class UserInsideToken {
        private Long id;
        private String email;
        private String name;
    }
}
```

### 5. Entity Relationships & Timestamps
- All entities extend or use `@PrePersist/@PreUpdate` for createdAt/updatedAt/createdBy/updatedBy
- `@ManyToOne` relationships use `@JoinColumn` for FK mapping
- `@OneToMany` uses `mappedBy` for reverse mapping (e.g., User-Resumes)
- Jobs → Skills: Many-to-Many via `JobSpecification` (avoid direct join table entity)

### 6. Dynamic Filtering
Uses **Spring Filter** library (`jpa:3.1.7`):
- Clients send filter queries: `?filter={field}=={value};{field2}>=={value2}`
- Repositories leverage `JobSpecification` for complex queries
- Example: `@RequestParam(required = false) String filter` → parse with `Specification<T>`

---

## Frontend Integration Patterns

### API Client Setup
**File**: `frontend/src/api/axiosClient.js`
- Base URL from `VITE_API_URL` env var (defaults to `http://127.0.0.1:8080/api/v1`)
- Credentials: `withCredentials: true` for cookie-based refresh token
- Automatic token refresh on 401 (avoid retry loops)

**Endpoints**: Centralized in `frontend/src/api/endpoints.js`
```javascript
JOBS: {
    BASE: `${API_BASE_URL}/jobs`,
    GET_ONE: (id) => `...`,
    POSTING_STATS: `...`,
}
```

### Authentication Context
- **File**: `frontend/src/context/AuthContext.jsx`
- Stores user, roles, permissions from login response
- Redirects unauthenticated users to /login via `useRoleRedirect` hook

### Layout System
Three protected layouts in `frontend/src/layouts/`:
- `AdminLayout.jsx` → Admin dashboard (read-only, stats)
- `RecruiterLayout.jsx` → HR dashboard (job postings, resume review)
- `CandidateLayout.jsx` → Candidate dashboard (applications, profile)

Role validation happens at route level via React Router.

---

## Configuration & Environment

### Backend Properties (application.properties)
Externalized via environment variables:
```properties
spring.datasource.url=${DB_URL}          # JDBC URL
jobhunter.jwt.base64-secret=${JWT_SECRET} # Base64-encoded HS512 key
spring.mail.username=${MAIL_USERNAME}    # Gmail for notifications
vnpay.tmnCode=${VNPAY_TMNCODE}          # Payment gateway
spring.ai.openai.api-key=${OPENAI_API_KEY} # Chatbot
jobhunter.cloudinary.*                   # File storage
```

**Development**: Use `.env` file (loaded via `spring.config.import`)
**Production**: Set Render environment variables directly

### Frontend Environment
- `VITE_API_URL`: Backend API endpoint
- No `.env.example` provided; check frontend/package.json for expected vars

---

## Development Workflows

### Build & Run
```bash
# Backend
./gradlew clean build      # Full build
./gradlew bootRun          # Dev server (port 8080)
./gradlew test             # Run tests

# Frontend
cd frontend
npm install && npm run dev # Dev server (Vite, port 5173)
npm run build              # Production build
npm run lint               # ESLint check
```

### Docker Deployment
```bash
# Build image locally
docker build -t jobhunter:latest .

# Multi-stage: Build stage uses Gradle 8.7-JDK21, skips tests (-x test)
# Optimized runtime: eclipse-temurin:21-jre-jammy
```

### IntelliJ Integration
- **Run Configuration**: Spring Boot application (port 8080)
- **Database**: Connect to MySQL/PostgreSQL via IDE datagrip plugin
- **Swagger**: Open [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) for API docs

---

## Key Implementation Details

### Payment Integration (VNPAY)
- `PaymentService` creates payment URLs
- Webhook callback → `PaymentController.vnpayReturn()`
- Updates `PaymentHistory` and user subscription status
- Related: `JobPostingService` validates posting limits per subscription tier

### Email & Notifications
- `EmailService` sends via Gmail SMTP
- Uses Thymeleaf templates in `src/main/resources/templates/`
- Triggered on: password reset, resume status changes, etc.

### File Storage (Cloudinary)
- `FileService` abstracts upload/download
- Strategy pattern: Can switch between Cloudinary and local filesystem
- Configured via `jobhunter.storage.type` property

### ChatBot (Spring AI + OpenAI)
- `ChatbotService` integrates Spring AI
- Frontend: Chatbot component in React
- Model: GPT-4o-mini (specified in application.properties)

### Job Expiry & Scheduling
- `JobExpiryScheduler` periodically marks expired jobs
- Scheduled tasks use Spring's `@Scheduled`

---

## Common Tasks & Where to Find Them

| Task | File(s) |
|------|---------|
| Add new API endpoint | Create method in Controller → Service → Repository layer |
| Modify authentication | SecurityConfiguration.java + AuthController.java |
| Add permission for role | Role-Permission associations → PermissionInterceptor checks |
| Handle new exception type | Define custom exception in util/error/ → Register in GlobalException handler |
| Add new entity | Create entity in domain/ → Repository (Spring Data JPA) → Service CRUD |
| Frontend form validation | React Hook Form + Yup schemas |
| Add new environment variable | Update application.properties + Dockerfile/render.yaml env vars |
| Schedule a task | Create @Service class with @Scheduled(cron = "...") method |

---

## Security Considerations

1. **CORS**: Configured in `CorsConfig` (or SecurityConfiguration), restricted to frontend origin
2. **CSRF**: Disabled for REST API (stateless, JWT-based)
3. **Password**: BCrypt hashed, expiration tracked in `passwordLastChangedAt`
4. **Email Verification**: Pending users have `status=PENDING` until email token validated
5. **Refresh Token**: HttpOnly, secure cookies prevent XSS attacks on token
6. **SQL Injection**: Spring JPA parameterized queries prevent SQLI

---

## Testing Approach
- Minimal test files present; integration tests focus on authentication
- Use `spring-boot-starter-test` + `spring-security-test` for mocking
- Test repository queries with `@DataJpaTest`

---

## Important Notes for AI Agents

1. **Always use DTOs**: Map entities to DTOs in service layer before returning to controller
2. **Validate early**: Use `@Valid` + `@NotNull/@NotBlank` on controller params
3. **Use transactions**: Mark service methods with `@Transactional` for multi-step operations
4. **Lazy load carefully**: Consider `FetchType.EAGER` for relationships if N+1 issues occur
5. **Mock external services**: VNPAY, Google OAuth, OpenAI should be behind interfaces for testing
6. **Version API**: Use `/api/v1/` prefix; prepare for `/api/v2/` if breaking changes needed
7. **Document DTOs**: Add Javadoc or Swagger annotations for clarity
8. **Frontend env vars**: Always check `import.meta.env.VITE_*` pattern for Vite config variables

---

## Useful Commands for Agents

```bash
# Backend build & test
./gradlew clean build -x test   # Fast build (skip tests)
./gradlew test --tests "*UserService*"  # Run specific tests

# Database inspection (localhost MySQL)
mysql -u root -p jobhunter
> SHOW TABLES;
> DESC users;

# Frontend build
npm run build --prefix frontend
npm run preview --prefix frontend  # Preview production build

# Docker compose (if available)
docker compose up -d  # Local dev environment
```

---

## References
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Security**: JWT configuration in `SecurityConfiguration.java`
- **React Best Practices**: See `frontend/src/layouts/` for routing patterns
- **API Spec**: Auto-generated at `/swagger-ui/index.html` during dev

