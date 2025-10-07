package org.example.user

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ProtectedDataResponse(
    val message: String,
    val userId: String,
    val email: String?,
    val timestamp: Long = System.currentTimeMillis()
)

fun Routing.configureUserRouting() {
    // Protected routes requiring JWT authentication
    authenticate("supabase-jwt") {
        route("/api/user") {
            // Example: Get user dashboard data
            get("/dashboard") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("sub")?.asString() ?: ""
                val email = principal?.payload?.getClaim("email")?.asString()

                call.respond(
                    HttpStatusCode.OK,
                    ProtectedDataResponse(
                        message = "Welcome to your dashboard!",
                        userId = userId,
                        email = email
                    )
                )
            }

            // Example: Get user settings
            get("/settings") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("sub")?.asString() ?: ""

                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "userId" to userId,
                        "settings" to mapOf(
                            "theme" to "dark",
                            "notifications" to true
                        )
                    )
                )
            }
        }
    }
}
