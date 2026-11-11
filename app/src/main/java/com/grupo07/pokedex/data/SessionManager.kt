package com.grupo07.pokedex.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Guarda o id do usuário logado em SharedPreferences.
 * Por isso a pessoa continua logada depois de fechar e abrir o app.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("sessao", Context.MODE_PRIVATE)

    private val _usuarioId = MutableStateFlow(
        prefs.getLong(CHAVE_USUARIO, SEM_USUARIO).takeIf { it != SEM_USUARIO }
    )

    /** null = ninguém logado */
    val usuarioId: StateFlow<Long?> = _usuarioId.asStateFlow()

    fun salvar(id: Long) {
        prefs.edit().putLong(CHAVE_USUARIO, id).apply()
        _usuarioId.value = id
    }

    fun limpar() {
        prefs.edit().remove(CHAVE_USUARIO).apply()
        _usuarioId.value = null
    }

    private companion object {
        const val CHAVE_USUARIO = "usuario_id"
        const val SEM_USUARIO = -1L
    }
}
