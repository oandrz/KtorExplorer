# Supabase JWT Authentication with Ktor Backend

Complete implementation guide for JWT token authentication with Supabase in a Ktor backend.

## Overview

This implementation provides:
- User registration with email/password
- User login returning JWT access and refresh tokens
- JWT token verification for protected routes
- Automatic token validation using Supabase's JWT secret
- Protected endpoints requiring authentication

## Prerequisites

1. **Supabase Project Setup**
   - Create a project at [supabase.com](https://supabase.com)
   - Enable Email authentication in Authentication settings
   - Get your project credentials

2. **Environment Variables**
   Add these to your `local.properties` file:
   ```properties
   SUPABASE_PROJECT_URL=https://your-project.supabase.co
   SUPABASE_API_KEY=your-anon-public-key
   SUPABASE_JWT_SECRET=your-jwt-secret
   ```

   You can find these values in your Supabase project:
   - **URL**: Project Settings → API → Project URL
   - **Anon Key**: Project Settings → API → anon/public key
   - **JWT Secret**: Project Settings → API → JWT Secret

## API Endpoints

### 1. Register New User

**POST** `/auth/register`

Request body:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "username": "johndoe"
}
```

Response (201 Created):
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "message": "User registered successfully. Please check your email for verification."
}
```

Error Response (400 Bad Request):
```json
{
  "error": "Email already registered"
}
```

### 2. Login

**POST** `/auth/login`

Request body:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

Response (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "v1.MRjVpMiq-qxxxxxxxxxxxxxxxxxxxxxzLw",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com"
}
```

**Important**: Save the `accessToken` - you'll need it for protected endpoints!

### 3. Get Current User Profile (Protected)

**GET** `/auth/me`

Headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Response (200 OK):
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com"
}
```

### 4. Logout (Protected)

**POST** `/auth/logout`

Headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Response (200 OK):
```json
{
  "message": "Logged out successfully"
}
```

### 5. Example Protected Endpoints

**GET** `/api/user/dashboard`

Headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Response (200 OK):
```json
{
  "message": "Welcome to your dashboard!",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "timestamp": 1704067200000
}
```

## Testing with cURL

### Register a new user
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "username": "testuser"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Access protected endpoint
```bash
# Replace YOUR_ACCESS_TOKEN with the token from login response
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Get dashboard data
```bash
curl -X GET http://localhost:8080/api/user/dashboard \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## JWT Token Details

The JWT token returned by Supabase contains these claims:

```json
{
  "aud": "authenticated",
  "role": "authenticated",
  "email": "user@example.com",
  "sub": "123e4567-e89b-12d3-a456-426614174000",
  "exp": 1704067200,
  "iat": 1704063600,
  "iss": "https://your-project.supabase.co/auth/v1"
}
```

- **sub**: User ID (unique identifier)
- **email**: User's email address
- **role**: User's role (authenticated, anon, etc.)
- **exp**: Token expiration timestamp
- **aud**: Audience claim (should be "authenticated")

## How It Works

1. **Registration Flow**:
   - User submits email, password, and username
   - Ktor calls Supabase Auth API to create user
   - Supabase sends verification email (if enabled)
   - Returns user ID and email

2. **Login Flow**:
   - User submits email and password
   - Ktor calls Supabase Auth API to authenticate
   - Supabase validates credentials and returns session
   - Session contains JWT access token and refresh token
   - Client stores access token for future requests

3. **Protected Route Access**:
   - Client sends request with `Authorization: Bearer <token>` header
   - Ktor JWT plugin verifies token using Supabase JWT secret
   - Extracts user info from JWT claims (sub, email, etc.)
   - Grants access if token is valid
   - Returns 401 Unauthorized if token is invalid/expired

## Security Best Practices

1. **Always use HTTPS in production** - JWT tokens in headers can be intercepted
2. **Store JWT secret securely** - Never commit to version control
3. **Implement token refresh** - Access tokens expire (default: 1 hour)
4. **Validate token on every request** - The JWT plugin does this automatically
5. **Use Row Level Security (RLS)** in Supabase - Additional database-level protection

## Token Expiration

- **Access Token**: Expires in 1 hour (default)
- **Refresh Token**: Expires in 7 days (default)

When access token expires:
1. Client receives 401 Unauthorized
2. Client should use refresh token to get new access token
3. Implement refresh endpoint or handle client-side

## Error Codes

| Code | Meaning |
|------|---------|
| 201  | User registered successfully |
| 200  | Login successful / Request successful |
| 400  | Bad request (invalid input, email already exists) |
| 401  | Unauthorized (invalid credentials, expired token) |
| 403  | Forbidden (valid token but insufficient permissions) |
| 500  | Internal server error |

## Code Structure

```
src/main/kotlin/org/example/
├── auth/
│   ├── JwtConfig.kt              # JWT configuration & verification
│   ├── AuthRouting.kt            # Auth endpoints (register, login, logout)
│   ├── model/
│   │   └── AuthData.kt           # Request/Response models
│   └── service/
│       └── UserService.kt        # Supabase auth integration
├── user/
│   └── UserRouting.kt            # Protected user endpoints
└── Application.kt                # Main app configuration
```

## Troubleshooting

### "Invalid JWT signature" error
- Verify `SUPABASE_JWT_SECRET` matches your Supabase project
- Check the JWT secret in Supabase Dashboard → Settings → API

### "Token expired" error
- Access tokens expire after 1 hour
- Implement token refresh logic
- User needs to login again

### Registration works but login fails
- Ensure email verification is disabled OR user verified their email
- Check Supabase Auth logs in dashboard

### 401 Unauthorized on protected routes
- Ensure `Authorization` header is set correctly
- Format: `Authorization: Bearer <token>`
- Token should not have extra quotes or spaces

## Additional Resources

- [Supabase Auth Documentation](https://supabase.com/docs/guides/auth)
- [Ktor JWT Authentication](https://ktor.io/docs/jwt.html)
- [Supabase Kotlin Client](https://github.com/supabase-community/supabase-kt)
