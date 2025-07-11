package org.example.org.example.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.pokemon.service.PokemonService

class PokemonInfoToolSet(
    private val pokemonInfoService: PokemonService
) : ToolSet {

    @Tool
    suspend fun providePokemonInfo(
        @LLMDescription("initial character of the pokemon")
        initCharacter: Char
    ): String = withContext(Dispatchers.IO) {
        val response = pokemonInfoService.getPokemonList()
        return@withContext if (response.isSuccess) {
            response.getOrNull()
                ?.results
                ?.filter { it.name.firstOrNull()?.lowercaseChar() == initCharacter.lowercaseChar() }
                ?.joinToString(", ") { it.name }
                ?: "No Pokémon found starting with '$initCharacter'"
        } else {
            "Failed to fetch Pokémon list: ${response.exceptionOrNull()?.message}"
        }
    }
}