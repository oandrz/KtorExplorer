package org.example.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserRequest(val email: String, val password: String, val username: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: String,
    val email: String
)

sealed class AuthError(message: String) : Exception(message) {
    data class WeakPassword(val details: String = "Password should be at least 6 characters") : AuthError(details)
    data class InvalidCredentials(val details: String = "Invalid email or password") : AuthError(details)
    data class EmailAlreadyExists(val details: String = "Email already registered") : AuthError(details)
    data class InvalidEmail(val details: String = "Invalid email format") : AuthError(details)
    data class NetworkError(val details: String = "Network error, please try again") : AuthError(details)
    data class Unknown(val details: String = "An unexpected error occurred") : AuthError(details)
}