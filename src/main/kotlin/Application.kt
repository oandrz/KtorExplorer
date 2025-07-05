package org.example

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.config.Config
import org.slf4j.LoggerFactory

// Create a logger instance for this class
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
        get("/ai") {
            val apiKey = try {
                Config.getRequiredProperty("GEMINI_API_KEY")
            } catch (e: IllegalStateException) {
                call.respondText("GEMINI_API_KEY not found in local.properties")
                return@get
            }

            try {
                val agent = AIAgent(
                    executor = simpleGoogleAIExecutor(apiKey),
                    systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
                    llmModel = GoogleModels.Gemini2_0Flash001
                )
                val result = agent.runAndGetResult("Hello! How can you help me?")
                call.respondText("AI Response: $result")
            } catch (e: Exception) {
                call.respondText("Error calling AI: ${e.message}")
            }
        }
    }
} 