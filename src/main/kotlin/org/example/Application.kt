package org.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.example.pokemon.service.PokemonService
import org.example.todo.TodoService
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")
private val todoService = TodoService()
private val pokemonService = PokemonService()

fun Application.module() {
    // Log application start
    logger.info("Starting application...")
    configureSerialization()
    monitor.subscribe(ApplicationStopping) {
        pokemonService.close()
    }
    configureRouting(
        pokemonService = pokemonService,
        todoService = todoService
    )
}

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}