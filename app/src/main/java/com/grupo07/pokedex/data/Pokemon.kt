package com.grupo07.pokedex.data

// Modelos que o app usa nas telas (já "limpos", sem a bagunça do JSON).

/** Item da lista: número, nome e imagem. */
data class PokemonResumo(
    val id: Int,
    val nome: String,
    val imagemUrl: String
)

/** Tudo o que a tela de detalhe mostra. */
data class PokemonDetalhe(
    val id: Int,
    val nome: String,
    val imagemUrl: String,
    val tipos: List<String>,     // nomes da API em inglês: "grass", "poison"...
    val alturaM: Double,
    val pesoKg: Double,
    val xpBase: Int,
    val atributos: List<Atributo>
)

/** Um atributo base: "hp" = 45, "attack" = 49... */
data class Atributo(
    val nome: String,
    val valor: Int
)

/** Imagem oficial de cada Pokémon, montada a partir do id. */
fun imagemDoPokemon(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

/** "bulbasaur" -> "Bulbasaur" */
fun String.capitalizar(): String = replaceFirstChar { it.uppercase() }

fun RecursoApi.paraResumo(): PokemonResumo =
    PokemonResumo(id = id, nome = name.capitalizar(), imagemUrl = imagemDoPokemon(id))

fun PokemonResposta.paraDetalhe(): PokemonDetalhe =
    PokemonDetalhe(
        id = id,
        nome = name.capitalizar(),
        imagemUrl = imagemDoPokemon(id),
        tipos = types.sortedBy { it.slot }.map { it.type.name },
        alturaM = height / 10.0,
        pesoKg = weight / 10.0,
        xpBase = baseExperience ?: 0,
        atributos = stats.map { Atributo(it.stat.name, it.baseStat) }
    )
