package org.example

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.example.auth.service.UserServiceImpl
import org.example.blog.service.BlogApiServiceImpl
import org.example.config.Config
import org.example.pokemon.service.PokemonService
import org.example.todo.TodoService
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")
private val todoService = TodoService()
private val pokemonService = PokemonService()
private const val SUPABASE_URL_KEY = "SUPABASE_PROJECT_URL"
private val SUPABASE_URL: String by lazy {
    // Load from local.properties via Config if present; otherwise fallback to env vars
    Config.getProperty(SUPABASE_URL_KEY)
        ?: System.getenv(SUPABASE_URL_KEY)
        ?: System.getProperty(SUPABASE_URL_KEY)
        ?: throw IllegalStateException("Supabase URL not configured. Set SUPABASE_PROJECT_URL in local.properties or SUPABASE_URL env/system property.")
}
private const val SUPABASE_API_KEY = "SUPABASE_API_KEY"
private val SUPABASE_KEY: String by lazy {
    Config.getProperty(SUPABASE_API_KEY)
        ?: System.getenv(SUPABASE_API_KEY)
        ?: System.getProperty(SUPABASE_API_KEY)
        ?: throw IllegalStateException("Supabase key not configured. Set SUPABASE_API_KEY in local.properties or SUPABASE_KEY env/system property.")
}

private val supabase by lazy {
    createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
}
private val blogApiService by lazy { BlogApiServiceImpl(supabase) }
private val userService by lazy { UserServiceImpl(supabase) }


fun Application.module() {
    // Log application start
    logger.info("Starting application...")
    configureSerialization()
    monitor.subscribe(ApplicationStopping) {
        pokemonService.close()
    }
    configureRouting(
        pokemonService = pokemonService,
        todoService = todoService,
        blogApiService = blogApiService,
        userService = userService
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