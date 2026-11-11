package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo07.pokedex.data.Favorito
import com.grupo07.pokedex.data.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel : ViewModel() {

    private val repositorio = ServiceLocator.pokemonRepository

    /** null enquanto o Room ainda não respondeu. */
    val favoritos: StateFlow<List<Favorito>?> = repositorio.favoritos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun remover(pokemonId: Int) {
        viewModelScope.launch { repositorio.removerFavorito(pokemonId) }
    }
}
