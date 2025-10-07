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
import org.example.auth.model.AuthError
import org.example.auth.model.AuthResponse
import org.example.auth.model.LoginRequest
import org.example.auth.model.RegisterUserRequest


interface UserService {
    suspend fun register(request: RegisterUserRequest): Either<AuthError, AuthResponse>
    suspend fun login(request: LoginRequest): Either<AuthError, AuthResponse>
    suspend fun logout(): Either<Throwable, Unit>
    suspend fun getCurrentUser(): Either<Throwable, String?>
}

class UserServiceImpl(private val supabaseClient: SupabaseClient) : UserService {

    override suspend fun register(request: RegisterUserRequest): Either<AuthError, AuthResponse> {
        return try {
            val result = supabaseClient.auth.signUpWith(Email) {
                email = request.email
                password = request.password
                data = buildJsonObject {
                    put("username", request.username)
                }
            }

            val authResponse = AuthResponse(
                userId = result?.id.orEmpty(),
                email = result?.email.orEmpty()
            )
            authResponse.right()
        } catch (e: RestException) {
            handleRestException(e, "Registration")
        } catch (e: Exception) {
            AuthError.NetworkError().left()
        }
    }

    override suspend fun login(request: LoginRequest): Either<AuthError, AuthResponse> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                email = request.email
                password = request.password
            }

            // Get the current session to access tokens
            val session = supabaseClient.auth.currentSessionOrNull()

            if (session != null) {
                val authResponse = AuthResponse(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    userId = session.user?.id.orEmpty(),
                    email = session.user?.email.orEmpty()
                )
                authResponse.right()
            } else {
                AuthError.InvalidCredentials("Failed to retrieve session").left()
            }
        } catch (e: RestException) {
            handleRestException(e, "Login")
        } catch (e: Exception) {
            AuthError.NetworkError().left()
        }
    }

    private fun handleRestException(e: RestException, operationName: String): Either<AuthError, Nothing> {
        val authError = when {
            e.message?.contains("weak_password", ignoreCase = true) == true ->
                AuthError.WeakPassword()
            e.message?.contains("email", ignoreCase = true) == true &&
                    e.message?.contains("already", ignoreCase = true) == true ->
                AuthError.EmailAlreadyExists()
            e.message?.contains("invalid_email", ignoreCase = true) == true ->
                AuthError.InvalidEmail()
            else -> AuthError.Unknown("$operationName failed: ${e.error}")
        }
        return authError.left()
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