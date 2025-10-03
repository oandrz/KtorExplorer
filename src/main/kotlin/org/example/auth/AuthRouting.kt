package org.example.auth

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.auth.service.RegisterUserRequest
import org.example.auth.service.UserService

private const val AUTH_ROUTE = "/auth"
private const val POST_PARAM_EMAIL = "email"
private const val POST_PARAM_PASSWORD = "password"
private const val POST_PARAM_USERNAME = "username"

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class SuccessResponse(val message: String)

internal fun Routing.configureAuthRouting(userService: UserService) {
    route(AUTH_ROUTE) {
        post("/register") {
            val params = call.receiveParameters()
            val email = params[POST_PARAM_EMAIL] ?: throw IllegalArgumentException("Email is required")
            val username = params[POST_PARAM_USERNAME] ?: throw IllegalArgumentException("Username is required")
            val password = params[POST_PARAM_PASSWORD] ?: throw IllegalArgumentException("Password is required")

            val result = userService.register(RegisterUserRequest(
                email = email,
                password = password,
                username = username
            ))
            if (result.isRight()) {
                call.respond(HttpStatusCode.Created, SuccessResponse("User registered successfully"))
            } else {
                val error = result.swap().getOrNull()
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(error?.message ?: "Error registering user")
                )
            }
        }
    }
}