package com.grupo07.pokedex.data

import android.content.Context
import com.grupo07.pokedex.network.RetrofitInstance

/**
 * Cria uma única vez o banco, a sessão e os repositórios
 * e entrega para quem precisar (os ViewModels).
 */
object ServiceLocator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val database by lazy { PokedexDatabase.getInstance(appContext) }

    private val sessao by lazy { SessionManager(appContext) }

    val usuarioRepository by lazy {
        UsuarioRepository(database.usuarioDao(), sessao)
    }

    val pokemonRepository by lazy {
        PokemonRepository(RetrofitInstance.api, database.favoritoDao(), sessao)
    }
}
