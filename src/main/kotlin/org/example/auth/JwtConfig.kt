package org.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.github.jan.supabase.SupabaseClient
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.example.config.Config

object JwtConfig {
    private const val JWT_SECRET_KEY = "SUPABASE_JWT_SECRET"

    val jwtSecret: String by lazy {
        Config.getProperty(JWT_SECRET_KEY)
            ?: System.getenv(JWT_SECRET_KEY)
            ?: System.getProperty(JWT_SECRET_KEY)
            ?: throw IllegalStateException("JWT Secret not configured. Set SUPABASE_JWT_SECRET in local.properties or env/system property.")
    }

    const val JWT_AUDIENCE = "authenticated"
    const val JWT_REALM = "ktor-supabase-app"

    fun Application.configureAuth(supabaseClient: SupabaseClient) {
        install(Authentication) {
            jwt("supabase-jwt") {
                realm = JWT_REALM

                verifier(
                    JWT.require(Algorithm.HMAC256(jwtSecret))
                        .withAudience(JWT_AUDIENCE)
                        .build()
                )

                validate { credential ->
                    // Validate the JWT token
                    if (credential.payload.audience.contains(JWT_AUDIENCE)) {
                        val userId = credential.payload.getClaim("sub").asString()
                        val email = credential.payload.getClaim("email").asString()

                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }

                challenge { _, _ ->
                    call.respondUnauthorized("Token is not valid or has expired")
                }
            }
        }
    }

    /**
     * Verify Supabase JWT token manually
     */
    fun verifySupabaseToken(token: String): DecodedJWT? {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withAudience(JWT_AUDIENCE)
                .build()

            verifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract user ID from JWT token
     */
    fun getUserIdFromToken(token: String): String? {
        return verifySupabaseToken(token)?.getClaim("sub")?.asString()
    }

    /**
     * Extract email from JWT token
     */
    fun getEmailFromToken(token: String): String? {
        return verifySupabaseToken(token)?.getClaim("email")?.asString()
    }
}

private suspend fun ApplicationCall.respondUnauthorized(message: String) {
    respond(
        io.ktor.http.HttpStatusCode.Unauthorized,
        mapOf("error" to message)
    )
}
