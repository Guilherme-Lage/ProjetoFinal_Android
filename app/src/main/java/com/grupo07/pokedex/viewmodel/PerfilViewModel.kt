package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo07.pokedex.data.ServiceLocator
import com.grupo07.pokedex.data.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PerfilViewModel : ViewModel() {

    private val usuarios = ServiceLocator.usuarioRepository
    private val pokemons = ServiceLocator.pokemonRepository

    val usuario: StateFlow<Usuario?> = usuarios.usuarioLogado
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val totalFavoritos: StateFlow<Int> = pokemons.totalFavoritos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun sair() = usuarios.sair()
}
