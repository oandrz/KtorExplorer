package org.example.pokemon.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.pokemon.model.PokemonDetails
import org.example.pokemon.model.PokemonEntry
import org.example.pokemon.model.PokemonListResponse

class PokemonService(private val baseUrl: String = "https://pokeapi.co/api/v2") {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Fetches a paginated list of Pokemon
     * @param limit Number of Pokemon to fetch per page
     * @param offset Number of Pokemon to skip
     */
    suspend fun getPokemonList(
        limit: Int = 1000,
        offset: Int = 0
    ): Result<PokemonListResponse> = runCatching {
        client.get("$baseUrl/pokemon") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    /**
     * Fetches detailed information about a specific Pokemon
     * @param idOrName The Pokemon's ID number or name
     */
    suspend fun getPokemonDetails(idOrName: String): Result<PokemonDetails> = runCatching {
        client.get("$baseUrl/pokemon/$idOrName").body()
    }

    /**
     * Fetches the next page of Pokemon results
     * @param currentResponse The current PokemonListResponse
     */
    suspend fun getNextPage(
        currentResponse: PokemonListResponse
    ): Result<PokemonListResponse>? = currentResponse.next?.let { nextUrl ->
        runCatching {
            client.get(nextUrl).body()
        }
    }

    /**
     * Fetches the previous page of Pokemon results
     * @param currentResponse The current PokemonListResponse
     */
    suspend fun getPreviousPage(
        currentResponse: PokemonListResponse
    ): Result<PokemonListResponse>? = currentResponse.previous?.let { previousUrl ->
        runCatching {
            client.get(previousUrl).body()
        }
    }

    /**
     * Closes the HTTP client when done
     */
    fun close() {
        println("testing andre close the method here")
        client.close()
    }

    suspend fun getPokemonNamesStartingWith(initCharacter: Char): List<PokemonEntry> {
        return getPokemonList().getOrNull()?.results
            ?.filter { it.name.startsWith(initCharacter, ignoreCase = true) } ?: emptyList()
    }
}
