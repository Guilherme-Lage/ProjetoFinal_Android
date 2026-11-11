package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import com.grupo07.pokedex.data.ServiceLocator

/** Usado na abertura do app para decidir se começa no Login ou na Pokedex. */
class SessaoViewModel : ViewModel() {
    val estaLogado: Boolean get() = ServiceLocator.usuarioRepository.estaLogado()
}
