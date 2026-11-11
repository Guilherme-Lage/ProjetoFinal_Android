package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo07.pokedex.data.PokemonDetalhe
import com.grupo07.pokedex.data.ServiceLocator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetalheEstado {
    data object Carregando : DetalheEstado
    data class Sucesso(val pokemon: PokemonDetalhe) : DetalheEstado
    data class Erro(val semConexao: Boolean) : DetalheEstado
}

@OptIn(ExperimentalCoroutinesApi::class)
class DetalheViewModel : ViewModel() {

    private val repositorio = ServiceLocator.pokemonRepository

    private val pokemonId = MutableStateFlow<Int?>(null)

    private val _estado = MutableStateFlow<DetalheEstado>(DetalheEstado.Carregando)
    val estado: StateFlow<DetalheEstado> = _estado.asStateFlow()

    /** Vem do Room: muda sozinho quando salva ou remove. */
    val ehFavorito: StateFlow<Boolean> = pokemonId
        .filterNotNull()
        .flatMapLatest { repositorio.ehFavorito(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /** A tela chama com o id que recebeu pela rota type-safe. */
    fun carregar(id: Int) {
        if (pokemonId.value == id && _estado.value is DetalheEstado.Sucesso) return
        pokemonId.value = id
        tentarDeNovo()
    }

    fun tentarDeNovo() {
        val id = pokemonId.value ?: return
        viewModelScope.launch {
            _estado.value = DetalheEstado.Carregando
            _estado.value = try {
                DetalheEstado.Sucesso(repositorio.detalhe(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DetalheEstado.Erro(semConexao = e is IOException)
            }
        }
    }

    fun alternarFavorito() {
        val pokemon = (_estado.value as? DetalheEstado.Sucesso)?.pokemon ?: return
        viewModelScope.launch {
            if (ehFavorito.value) repositorio.removerFavorito(pokemon.id)
            else repositorio.adicionarFavorito(pokemon)
        }
    }
}
