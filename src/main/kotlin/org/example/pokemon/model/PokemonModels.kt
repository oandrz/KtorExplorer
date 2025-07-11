package org.example.pokemon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a paginated response from the Pokemon API
 * @property count Total number of Pokemon available
 * @property next URL to the next page of results, or null if this is the last page
 * @property previous URL to the previous page of results, or null if this is the first page
 * @property results List of Pokemon entries in the current page
 */
@Serializable
data class PokemonListResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokemonEntry>
)

/**
 * Represents a single Pokemon entry in the list
 * @property name The name of the Pokemon
 * @property url The URL to get detailed information about this Pokemon
 */
@Serializable
data class PokemonEntry(
    val name: String,
    val url: String
)

/**
 * Represents detailed information about a Pokemon
 * (This is a placeholder - you can expand this based on the actual detailed response)
 */
@Serializable
data class PokemonDetails(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeSlot>,
    val sprites: PokemonSprites
    // Add more fields as needed from the detailed response
)

@Serializable
data class PokemonTypeSlot(
    val slot: Int,
    val type: NamedApiResource
)

@Serializable
data class PokemonSprites(
    val front_default: String? = null,
    val front_shiny: String? = null,
    val front_female: String? = null,
    val front_shiny_female: String? = null,
    val back_default: String? = null,
    val back_shiny: String? = null,
    val back_female: String? = null,
    val back_shiny_female: String? = null,
    val other: OtherSprites? = null
)

@Serializable
data class OtherSprites(
    @SerialName("official-artwork")
    val officialArtwork: OfficialArtwork? = null
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default")
    val frontDefault: String? = null,
    @SerialName("front_shiny")
    val frontShiny: String? = null
)

/**
 * Generic named API resource used in many Pokemon API responses
 */
@Serializable
data class NamedApiResource(
    val name: String,
    val url: String
)

/**
 * Helper function to extract Pokemon ID from URL
 */
fun PokemonEntry.extractId(): Int? {
    return """/(\d+)/?$""".toRegex()
        .find(url)
        ?.groupValues
        ?.get(1)
        ?.toIntOrNull()
}
