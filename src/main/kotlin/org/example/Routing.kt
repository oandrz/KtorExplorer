package org.example

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.blog.configureBlogRouting
import org.example.blog.service.BlogApiService
import org.example.config.Config
import org.example.org.example.tools.PokemonInfoTool
import org.example.org.util.AIPostParamsKey.USER_PROMPT_KEY
import org.example.pokemon.service.PokemonService
import org.example.todo.TodoService
import org.slf4j.LoggerFactory

private const val GEMINI_API_KEY = "GEMINI_API_KEY"
private const val OPEN_AI_API_KEY = "GPT_KEY"
private const val SYSTEM_PROMPT = """
You are a helpful assistant.
Your response MUST be a single valid JSON object, and nothing else.
Do NOT use Markdown formatting (no triple backticks, no ```json, etc).
Do NOT include any explanations, comments, or text outside the JSON.
If you need to use tools, use the tool registry as needed.
Return ONLY the JSON object, with no extra formatting or text.
"""
private val logger = LoggerFactory.getLogger("Routing")

internal fun Application.configureRouting(
    pokemonService: PokemonService,
    todoService: TodoService,
    blogApiService: BlogApiService
) {
    routing {
        configureDefaultRouting()
        configureAIRouting(pokemonService = pokemonService)
        configureTodoRouting(todoService = todoService)
        configureBlogRouting(blogApiService = blogApiService)
    }
}

private fun Routing.configureDefaultRouting() {
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
}

private fun Routing.configureAIRouting(pokemonService: PokemonService) {
    // Keep the AI endpoint separate
    route("/ai") {
        // Log the AI endpoint access
        logger.info("Accessing AI endpoint")
        // Provide a simple GET endpoint for /ai to avoid 404 and show usage
        get {
            call.respond(
                message = "AI endpoint is live. Use POST /ai/query with form parameter 'userPrompt' to query the AI.",
                status = HttpStatusCode.OK
            )
        }
        post("/query") {
            val apiKey = Config.getProperty(OPEN_AI_API_KEY)
                ?: System.getenv(OPEN_AI_API_KEY)
                ?: System.getProperty(OPEN_AI_API_KEY)
            val params = call.receiveParameters()
            val userPrompt = params[USER_PROMPT_KEY] ?: ""

            try {
                val agent = AIAgent(
                    executor = simpleOpenAIExecutor(apiKey),
                    systemPrompt = SYSTEM_PROMPT,
                    llmModel = OpenAIModels.Chat.GPT4_1,
                    toolRegistry = ToolRegistry {
                        tool(SayToUser)
                        tool(PokemonInfoTool(pokemonApiService = pokemonService))
                    }
                )
                val result = agent.run(userPrompt)
                call.respondText(result.toString(), ContentType.Application.Json)
                // Try to parse the result as JSON, fallback to error if not valid
//                try {
//                    val jsonElement = Json.parseToJsonElement(result.orEmpty())
//                    call.respond(jsonElement)
//                } catch (e: Exception) {
//                    logger.error("AI did not return valid JSON: $result", e)
//                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "AI did not return valid JSON", "raw" to result))
//                }
            } catch (e: Exception) {
                logger.error("Error calling AI", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Unknown error")))
            }
        }
    }
}

private fun Routing.configureTodoRouting(todoService: TodoService) {
    // Todo API endpoints
    route("/todos") {
        // Get all todos
        get {
            try {
                val todos = todoService.getAllTodos()
                call.respond(todos)
            } catch (e: Exception) {
                logger.error("Error getting todos", e)
                call.respond(HttpStatusCode.InternalServerError, "Error getting todos")
            }
        }

        // Get a single todo by ID
        get("/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing todo ID")
                return@get
            }

            try {
                val todo = todoService.getTodoById(id)
                if (todo != null) {
                    call.respond(todo)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Todo not found")
                }
            } catch (e: Exception) {
                logger.error("Error getting todo $id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error getting todo")
            }
        }

        // Create a new todo
        post {
            try {
                val params = call.receive<Map<String, String>>()
                val title = params["title"] ?: throw IllegalArgumentException("Title is required")

                val newTodo = todoService.addTodo(title)
                call.respond(HttpStatusCode.Created, newTodo)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
            } catch (e: Exception) {
                logger.error("Error creating todo", e)
                call.respond(HttpStatusCode.InternalServerError, "Error creating todo")
            }
        }

        // Toggle todo completion status
        put("/{id}/toggle") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing todo ID")
                return@put
            }

            try {
                val updatedTodo = todoService.toggleTodo(id)
                if (updatedTodo != null) {
                    call.respond(updatedTodo)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Todo not found")
                }
            } catch (e: Exception) {
                logger.error("Error toggling todo $id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error toggling todo")
            }
        }

        // Delete a todo
        delete("/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing todo ID")
                return@delete
            }

            try {
                val deleted = todoService.deleteTodo(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Todo not found")
                }
            } catch (e: Exception) {
                logger.error("Error deleting todo $id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error deleting todo")
            }
        }
    }
}

