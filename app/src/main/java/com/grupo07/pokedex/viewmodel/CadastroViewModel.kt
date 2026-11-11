package com.grupo07.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo07.pokedex.data.Campo
import com.grupo07.pokedex.data.ResultadoAuth
import com.grupo07.pokedex.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CadastroUiState(
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmarSenha: String = "",
    val carregando: Boolean = false,
    val erro: String? = null,
    val campoComErro: Campo? = null,
    val sucesso: Boolean = false
)

class CadastroViewModel : ViewModel() {

    private val repositorio = ServiceLocator.usuarioRepository

    private val _uiState = MutableStateFlow(CadastroUiState())
    val uiState: StateFlow<CadastroUiState> = _uiState.asStateFlow()

    fun onNomeChange(v: String) = _uiState.update { it.copy(nome = v, erro = null, campoComErro = null) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, erro = null, campoComErro = null) }
    fun onSenhaChange(v: String) = _uiState.update { it.copy(senha = v, erro = null, campoComErro = null) }
    fun onConfirmarChange(v: String) = _uiState.update { it.copy(confirmarSenha = v, erro = null, campoComErro = null) }

    fun cadastrar() {
        if (_uiState.value.carregando) return
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, erro = null, campoComErro = null) }
            val s = _uiState.value
            when (val r = repositorio.cadastrar(s.nome, s.email, s.senha, s.confirmarSenha)) {
                is ResultadoAuth.Sucesso ->
                    _uiState.update { it.copy(carregando = false, sucesso = true) }
                is ResultadoAuth.Falha ->
                    _uiState.update { it.copy(carregando = false, erro = r.mensagem, campoComErro = r.campo) }
            }
        }
    }

    fun sucessoTratado() = _uiState.update { CadastroUiState() }
}
