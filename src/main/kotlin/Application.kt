package org.example

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.config.Config
import org.example.util.AIPostParamsKey.USER_PROMPT_KEY
import org.slf4j.LoggerFactory

// Create a logger instance for this class
private const val GEMINI_API_KEY = "GEMINI_API_KEY"
private val logger = LoggerFactory.getLogger("Application")

fun Application.module() {
    // Log application start
    logger.info("Starting Ktor application")

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            try {
                // Simple request logging
                logger.info("Handling GET request to root path")

                val response = "Hello, Ktor! The server is running correctly!"
                logger.info("Sending response: $response")

                call.respondText(response)

                // Log successful response
                logger.info("Successfully handled request to root path")
            } catch (e: Exception) {
                // Error logging
                logger.error("Error handling request", e)
                call.respondText(
                    "An error occurred: ${e.message}",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }

        // Keep the AI endpoint separate
        post("/ai") {
            val apiKey = try {
                Config.getRequiredProperty(GEMINI_API_KEY)
            } catch (e: IllegalStateException) {
                call.respondText("GEMINI_API_KEY not found in local.properties")
                return@post
            }
            val params = call.receiveParameters()
            val userPrompt = params[USER_PROMPT_KEY] ?: ""

            try {
                val agent = AIAgent(
                    executor = simpleGoogleAIExecutor(apiKey),
                    systemPrompt = "You are a helpful assistant. You will echo what the user says, use the tools available to help you do the echo.",
                    llmModel = GoogleModels.Gemini2_0Flash001,
                    toolRegistry = ToolRegistry {
                        tool(SayToUser)
                    }
                )
                val result = agent.runAndGetResult(userPrompt)
                call.respondText("AI Response: $result")
            } catch (e: Exception) {
                call.respondText("Error calling AI: ${e.message}")
            }
        }
    }
} 