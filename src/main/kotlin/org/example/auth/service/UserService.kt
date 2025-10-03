package org.example.auth.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class RegisterUserRequest(val email: String, val password: String, val username: String)
data class LoginRequest(val email: String, val password: String)

sealed class AuthError(message: String) : Exception(message) {
    data class WeakPassword(val details: String = "Password should be at least 6 characters") : AuthError(details)
    data class InvalidCredentials(val details: String = "Invalid email or password") : AuthError(details)
    data class EmailAlreadyExists(val details: String = "Email already registered") : AuthError(details)
    data class InvalidEmail(val details: String = "Invalid email format") : AuthError(details)
    data class NetworkError(val details: String = "Network error, please try again") : AuthError(details)
    data class Unknown(val details: String = "An unexpected error occurred") : AuthError(details)
}

interface UserService {
    suspend fun register(request: RegisterUserRequest): Either<AuthError, String>
    suspend fun login(request: LoginRequest): Either<Throwable, String>
    suspend fun logout(): Either<Throwable, Unit>
    suspend fun getCurrentUser(): Either<Throwable, String?>
}

class UserServiceImpl(private val supabaseClient: SupabaseClient) : UserService {

    override suspend fun register(request: RegisterUserRequest): Either<AuthError, String> {
        return try {
            val result = supabaseClient.auth.signUpWith(Email) {
                email = request.email
                password = request.password
                data = buildJsonObject {
                    put("username", request.username)
                }
            }
            "Registration successful".right()
        } catch (e: RestException) {
            val authError = when {
                e.message?.contains("weak_password", ignoreCase = true) == true ->
                    AuthError.WeakPassword()
                e.message?.contains("email", ignoreCase = true) == true &&
                    e.message?.contains("already", ignoreCase = true) == true ->
                    AuthError.EmailAlreadyExists()
                e.message?.contains("invalid_email", ignoreCase = true) == true ->
                    AuthError.InvalidEmail()
                else -> AuthError.Unknown("Registration failed: ${e.error}")
            }
            authError.left()
        } catch (e: Exception) {
            AuthError.NetworkError().left()
        }
    }

    override suspend fun login(request: LoginRequest): Either<Throwable, String> {
//        return try {
//            val result = supabaseClient.auth.signInWith(Email) {
//                email = request.email
//                password = request.password
//            }
//            result.user?.id?.right() ?: "Login successful".right()
//        } catch (e: Exception) {
//            e.left()
//        }

        return "Login successful".right()
    }

    override suspend fun logout(): Either<Throwable, Unit> {
        return try {
            supabaseClient.auth.signOut()
            Unit.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun getCurrentUser(): Either<Throwable, String?> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull()
            user?.id.right()
        } catch (e: Exception) {
            e.left()
        }
    }
}