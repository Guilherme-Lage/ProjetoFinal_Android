package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo07.pokedex.data.ResultadoAuth
import com.grupo07.pokedex.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val carregando: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val repositorio = ServiceLocator.usuarioRepository

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(valor: String) = _uiState.update { it.copy(email = valor, erro = null) }

    fun onSenhaChange(valor: String) = _uiState.update { it.copy(senha = valor, erro = null) }

    fun entrar() {
        if (_uiState.value.carregando) return
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, erro = null) }
            val estado = _uiState.value
            when (val resultado = repositorio.entrar(estado.email, estado.senha)) {
                is ResultadoAuth.Sucesso ->
                    _uiState.update { it.copy(carregando = false, sucesso = true) }
                is ResultadoAuth.Falha ->
                    _uiState.update { it.copy(carregando = false, erro = resultado.mensagem) }
            }
        }
    }

    /** Chamado pela tela depois de navegar, para não navegar duas vezes. */
    fun sucessoTratado() = _uiState.update { LoginUiState() }
}
