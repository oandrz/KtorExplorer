package org.example.org.example.tools

import ai.koog.agents.core.tools.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.example.pokemon.service.PokemonService

class PokemonInfoTool(
    private val pokemonApiService: PokemonService
) : SimpleTool<PokemonInfoTool.Args>() {

    @Serializable
    data class Args(
        val initCharacter: Char
    ) : Tool.Args

    override val argsSerializer: KSerializer<Args> = Args.serializer()

    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "pokemon_info",
        description = "get every pokemon related information, return in JSON format",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "initCharacter",
                description = "The initial character to filter Pokemon names",
                type = ToolParameterType.String,
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        return withContext(Dispatchers.IO) {
            // Fetch Pokemon names starting with the specified character
            val pokemonNames = pokemonApiService.getPokemonNamesStartingWith(args.initCharacter)

            if (pokemonNames.isEmpty()) {
                "No Pokemon found starting with '${args.initCharacter}'"
            } else {
                "Pokemon starting with '${args.initCharacter}':\n" + pokemonNames.joinToString("\n")
            }
        }
    }
}