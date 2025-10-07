package org.example.auth

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.auth.model.LoginRequest
import org.example.auth.model.RegisterUserRequest
import org.example.auth.service.UserService

private const val AUTH_ROUTE = "/auth"

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class SuccessResponse(val message: String)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String?
)

@Serializable
data class RegisterResponse(
    val userId: String,
    val email: String?,
    val message: String
)

@Serializable
data class UserProfileResponse(
    val userId: String,
    val email: String?
)

internal fun Routing.configureAuthRouting(userService: UserService) {
    route(AUTH_ROUTE) {
        // Registration endpoint
        post("/register") {
            val request = call.receive<RegisterUserRequest>()

            val result = userService.register(request)
            if (result.isRight()) {
                val authResponse = result.getOrNull()!!
                call.respond(
                    HttpStatusCode.Created,
                    RegisterResponse(
                        userId = authResponse.userId,
                        email = authResponse.email,
                        message = "User registered successfully. Please check your email for verification."
                    )
                )
            } else {
                val error = result.swap().getOrNull()
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(error?.message ?: "Error registering user")
                )
            }
        }

        // Login endpoint - returns JWT tokens
        post("/login") {
            val request = call.receive<LoginRequest>()

            val result = userService.login(request)
            if (result.isRight()) {
                val authResponse = result.getOrNull()!!
                call.respond(
                    HttpStatusCode.OK,
                    LoginResponse(
                        accessToken = authResponse.accessToken!!,
                        refreshToken = authResponse.refreshToken!!,
                        userId = authResponse.userId,
                        email = authResponse.email
                    )
                )
            } else {
                val error = result.swap().getOrNull()
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error?.message ?: "Invalid credentials")
                )
            }
        }

        // Logout endpoint (requires authentication)
        authenticate("supabase-jwt") {
            post("/logout") {
                val result = userService.logout()
                if (result.isRight()) {
                    call.respond(HttpStatusCode.OK, SuccessResponse("Logged out successfully"))
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Error logging out")
                    )
                }
            }

            // Get current user profile (protected endpoint)
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("sub")?.asString()
                val email = principal?.payload?.getClaim("email")?.asString()

                if (userId != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        UserProfileResponse(userId = userId, email = email)
                    )
                } else {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid token"))
                }
            }
        }
    }
}