package com.grupo07.pokedex.data

import com.grupo07.pokedex.network.PokeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Único lugar que fala com a API (Retrofit) e com os favoritos (Room).
 * Os ViewModels só conversam com o Repository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PokemonRepository(
    private val api: PokeApiService,
    private val favoritoDao: FavoritoDao,
    private val sessao: SessionManager
) {

    // Cache em memória: trocar de aba não refaz a requisição
    private var cacheLista: List<PokemonResumo>? = null
    private val cacheTipos = mutableMapOf<String, Set<Int>>()

    // ---------- API ----------

    suspend fun listarPokemon(): List<PokemonResumo> =
        cacheLista ?: api.listarPokemon(limite = LIMITE)
            .results
            .map { it.paraResumo() }
            .also { cacheLista = it }

    /** Ids dos Pokémon de um tipo (ex.: "fire"), para o filtro da lista. */
    suspend fun idsDoTipo(tipo: String): Set<Int> =
        cacheTipos[tipo] ?: api.buscarTipo(tipo)
            .pokemon
            .map { it.pokemon.id }
            .toSet()
            .also { cacheTipos[tipo] = it }

    suspend fun detalhe(id: Int): PokemonDetalhe =
        api.buscarPokemon(id).paraDetalhe()

    // ---------- Room (favoritos do usuário logado) ----------

    val favoritos: Flow<List<Favorito>> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else favoritoDao.listar(id)
    }

    val totalFavoritos: Flow<Int> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(0) else favoritoDao.contar(id)
    }

    fun ehFavorito(pokemonId: Int): Flow<Boolean> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(false) else favoritoDao.ehFavorito(id, pokemonId)
    }

    suspend fun adicionarFavorito(pokemon: PokemonDetalhe) {
        val usuarioId = sessao.usuarioId.value ?: return
        favoritoDao.inserir(
            Favorito(
                usuarioId = usuarioId,
                pokemonId = pokemon.id,
                nome = pokemon.nome,
                imagemUrl = pokemon.imagemUrl,
                tipos = pokemon.tipos.joinToString(",")
            )
        )
    }

    suspend fun removerFavorito(pokemonId: Int) {
        val usuarioId = sessao.usuarioId.value ?: return
        favoritoDao.remover(usuarioId, pokemonId)
    }

    private companion object {
        const val LIMITE = 151 // primeira geração
    }
}
