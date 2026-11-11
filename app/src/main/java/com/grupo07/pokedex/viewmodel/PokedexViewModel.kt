package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo07.pokedex.data.PokemonResumo
import com.grupo07.pokedex.data.ServiceLocator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

/** O que a lista está mostrando agora: carregando, sucesso ou erro. */
sealed interface ListaEstado {
    data object Carregando : ListaEstado
    data class Sucesso(val pokemons: List<PokemonResumo>) : ListaEstado
    data class Erro(val semConexao: Boolean) : ListaEstado
}

data class PokedexUiState(
    val estado: ListaEstado = ListaEstado.Carregando,
    val busca: String = "",
    val tipoSelecionado: String? = null   // null = "Todos"
)

class PokedexViewModel : ViewModel() {

    private val repositorio = ServiceLocator.pokemonRepository

    private var todos: List<PokemonResumo> = emptyList()
    private var idsDoTipo: Set<Int>? = null

    private val _uiState = MutableStateFlow(PokedexUiState())
    val uiState: StateFlow<PokedexUiState> = _uiState.asStateFlow()

    /** Primeiro nome do usuário logado, para o "Olá, Fulano". */
    val primeiroNome: StateFlow<String> = ServiceLocator.usuarioRepository.usuarioLogado
        .map { it?.nome?.substringBefore(' ') ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _uiState.update { it.copy(estado = ListaEstado.Carregando) }
            try {
                todos = repositorio.listarPokemon()
                val tipo = _uiState.value.tipoSelecionado
                if (tipo != null && idsDoTipo == null) idsDoTipo = repositorio.idsDoTipo(tipo)
                aplicarFiltros()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(estado = ListaEstado.Erro(semConexao = e is IOException)) }
            }
        }
    }

    fun onBuscaChange(texto: String) {
        _uiState.update { it.copy(busca = texto) }
        if (todos.isNotEmpty()) aplicarFiltros()
    }

    fun selecionarTipo(tipo: String?) {
        _uiState.update { it.copy(tipoSelecionado = tipo) }
        idsDoTipo = null
        if (tipo == null) {
            if (todos.isNotEmpty()) aplicarFiltros()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(estado = ListaEstado.Carregando) }
            try {
                idsDoTipo = repositorio.idsDoTipo(tipo)
                if (todos.isEmpty()) todos = repositorio.listarPokemon()
                aplicarFiltros()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(estado = ListaEstado.Erro(semConexao = e is IOException)) }
            }
        }
    }

    /** Filtra a lista em memória pela busca (nome ou número) e pelo tipo. */
    private fun aplicarFiltros() {
        val termo = _uiState.value.busca.trim().lowercase().removePrefix("#")
        val numero = termo.toIntOrNull()
        val tipo = idsDoTipo
        val filtrada = todos.filter { p ->
            val passaTipo = tipo == null || p.id in tipo
            val passaBusca = termo.isEmpty() ||
                p.nome.lowercase().contains(termo) ||
                (numero != null && p.id == numero)
            passaTipo && passaBusca
        }
        _uiState.update { it.copy(estado = ListaEstado.Sucesso(filtrada)) }
    }
}
