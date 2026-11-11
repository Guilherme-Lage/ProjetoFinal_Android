package com.grupo07.pokedex.network

import com.grupo07.pokedex.data.ListaPokemonResposta
import com.grupo07.pokedex.data.PokemonResposta
import com.grupo07.pokedex.data.TipoResposta
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Endpoints da PokéAPI que o app usa.
 * As funções são suspend: o Retrofit faz a requisição fora da Main Thread.
 */
interface PokeApiService {

    @GET("pokemon")
    suspend fun listarPokemon(
        @Query("limit") limite: Int,
        @Query("offset") inicio: Int = 0
    ): ListaPokemonResposta

    @GET("pokemon/{id}")
    suspend fun buscarPokemon(@Path("id") id: Int): PokemonResposta

    @GET("type/{nome}")
    suspend fun buscarTipo(@Path("nome") nome: String): TipoResposta
}
