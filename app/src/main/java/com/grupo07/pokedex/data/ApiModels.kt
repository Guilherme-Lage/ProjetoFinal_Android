package com.grupo07.pokedex.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Data classes que espelham o JSON da PokéAPI.
// Só declaramos os campos que usamos; o resto é ignorado (ignoreUnknownKeys no Retrofit).

/** Item genérico da PokéAPI: { "name": "...", "url": "..." } */
@Serializable
data class RecursoApi(
    val name: String,
    val url: String
) {
    /** A URL termina com o id: ".../pokemon/25/" -> 25 */
    val id: Int get() = url.trimEnd('/').substringAfterLast('/').toInt()
}

/** GET pokemon?limit=151 */
@Serializable
data class ListaPokemonResposta(
    val count: Int = 0,
    val results: List<RecursoApi> = emptyList()
)

/** GET pokemon/{id} */
@Serializable
data class PokemonResposta(
    val id: Int,
    val name: String,
    val height: Int,                       // em decímetros
    val weight: Int,                       // em hectogramas
    @SerialName("base_experience") val baseExperience: Int? = null,
    val types: List<TipoSlot> = emptyList(),
    val stats: List<StatSlot> = emptyList()
)

@Serializable
data class TipoSlot(
    val slot: Int,
    val type: RecursoApi
)

@Serializable
data class StatSlot(
    @SerialName("base_stat") val baseStat: Int,
    val stat: RecursoApi
)

/** GET type/{nome} -> lista de Pokémon daquele tipo */
@Serializable
data class TipoResposta(
    val name: String,
    val pokemon: List<PokemonDoTipo> = emptyList()
)

@Serializable
data class PokemonDoTipo(
    val pokemon: RecursoApi
)
