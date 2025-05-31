# SANATANA-DHARM Spring Boot Application

## Overview
A complete Spring Boot application with JWT-based authentication, role-based authorization, and MySQL database integration.

## Application Details
- **Name**: SANATANA-DHARM
- **Port**: 12000
- **Context Path**: /api
- **Database**: MySQL (MariaDB)
- **Authentication**: JWT Tokens
- **Security**: Spring Security with BCrypt password encoding

## Roles
The application supports four user roles:
1. **USER** - Basic user access
2. **SUPER_USER** - Enhanced user privileges
3. **ADMIN** - Administrative access
4. **SUPER_ADMIN** - Full system access

## Default Credentials
- **Username**: superadmin
- **Password**: SuperAdmin@123
- **Email**: superadmin@sanatanadharm.com
- **Role**: SUPER_ADMIN

## API Endpoints

### Public Endpoints
- `GET /api/test/all` - Public content (no authentication required)

### Authentication Endpoints
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `POST /api/auth/forgot-password` - Forgot password
- `POST /api/auth/reset-password` - Reset password
- `POST /api/auth/verify-email` - Email verification
- `POST /api/auth/refresh-token` - Refresh JWT token

### Protected Endpoints (Require Authentication)

#### Test Endpoints
- `GET /api/test/user` - User content (USER+ roles)
- `GET /api/test/admin` - Admin content (ADMIN+ roles)
- `GET /api/test/superuser` - Super user content (SUPER_USER+ roles)

#### User Endpoints
- `GET /api/user/profile` - Get current user profile
- `PUT /api/user/profile` - Update user profile
- `POST /api/user/change-password` - Change password

#### Admin Endpoints (ADMIN/SUPER_ADMIN only)
- `GET /api/admin/dashboard` - Admin dashboard with statistics
- `GET /api/admin/users` - Get all users (paginated)
- `GET /api/admin/users/{id}` - Get user by ID
- `PUT /api/admin/users/{id}/enable` - Enable user account
- `PUT /api/admin/users/{id}/disable` - Disable user account
- `GET /api/admin/roles` - Get all roles

## Request/Response Examples

### Login Request
```json
POST /api/auth/signin
{
  "usernameOrEmail": "superadmin",
  "password": "SuperAdmin@123"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "username": "superadmin",
  "email": "superadmin@sanatanadharm.com",
  "firstName": "Super",
  "lastName": "Admin",
  "roles": ["ROLE_SUPER_ADMIN"]
}
```

### Registration Request
```json
POST /api/auth/signup
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "NewUser@123",
  "firstName": "New",
  "lastName": "User"
}
```

### User Profile Response
```json
GET /api/user/profile
{
  "id": 1,
  "username": "superadmin",
  "email": "superadmin@sanatanadharm.com",
  "firstName": "Super",
  "lastName": "Admin",
  "phoneNumber": null,
  "emailVerified": true,
  "createdAt": "2025-05-31T10:22:43",
  "lastLogin": "2025-05-31T10:31:29",
  "roles": ["Super Admin"]
}
```

### Admin Dashboard Response
```json
GET /api/admin/dashboard
{
  "totalUsers": 2,
  "activeUsers": 2,
  "unverifiedUsers": 1,
  "newUsersThisMonth": 2,
  "message": "Admin Dashboard - SANATANA-DHARM"
}
```

## Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Database Schema

### Users Table
- id (Primary Key)
- username (Unique)
- email (Unique)
- password (BCrypt encoded)
- firstName
- lastName
- phoneNumber
- isEnabled
- isAccountNonExpired
- isAccountNonLocked
- isCredentialsNonExpired
- emailVerified
- resetToken
- resetTokenExpiry
- verificationToken
- verificationTokenExpiry
- lastLogin
- createdAt
- updatedAt

### Roles Table
- id (Primary Key)
- name (Enum: USER, SUPER_USER, ADMIN, SUPER_ADMIN)
- description
- createdAt
- updatedAt

### User_Roles Table (Many-to-Many)
- user_id (Foreign Key)
- role_id (Foreign Key)

## Security Features
1. **JWT Authentication** - Stateless token-based authentication
2. **Password Encryption** - BCrypt with salt rounds
3. **Role-based Authorization** - Method-level security
4. **CORS Configuration** - Cross-origin resource sharing
5. **Input Validation** - Request validation with custom messages
6. **Global Exception Handling** - Centralized error handling
7. **Password Reset** - Secure token-based password reset
8. **Email Verification** - Account verification via email

## Configuration
The application uses `application.yml` for configuration:
- Database connection settings
- JWT secret and expiration
- Email service configuration
- Server port and context path

## Testing
The application has been tested with:
- ✅ User registration and login
- ✅ JWT token generation and validation
- ✅ Role-based access control
- ✅ Admin endpoints functionality
- ✅ User profile management
- ✅ Database operations
- ✅ Error handling

## Email Service
Email functionality is configured but requires a real SMTP service for production use. Currently using placeholder configuration.

## Production Considerations
1. Configure real SMTP service for email functionality
2. Use environment variables for sensitive configuration
3. Set up proper SSL/TLS certificates
4. Configure database connection pooling
5. Set up monitoring and logging
6. Implement rate limiting
7. Add API documentation with Swagger/OpenAPI

## Access URLs
- Application: https://work-1-nedwsbjkdouwcmux.prod-runtime.all-hands.dev
- API Base: https://work-1-nedwsbjkdouwcmux.prod-runtime.all-hands.dev/api