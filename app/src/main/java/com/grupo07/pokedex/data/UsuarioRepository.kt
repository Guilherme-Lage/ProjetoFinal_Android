package com.grupo07.pokedex.data

import android.database.sqlite.SQLiteConstraintException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/** Campo do formulário que causou o erro (para pintar só ele de vermelho). */
enum class Campo { NOME, EMAIL, SENHA, CONFIRMAR_SENHA }

sealed interface ResultadoAuth {
    data object Sucesso : ResultadoAuth
    data class Falha(val mensagem: String, val campo: Campo? = null) : ResultadoAuth
}

/**
 * Toda a regra de cadastro e login fica aqui, nunca dentro da tela.
 */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val sessao: SessionManager
) {

    private val regexEmail = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuarioLogado: Flow<Usuario?> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(null) else usuarioDao.observarPorId(id)
    }

    fun estaLogado(): Boolean = sessao.usuarioId.value != null

    suspend fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        confirmarSenha: String
    ): ResultadoAuth {
        val emailLimpo = email.trim().lowercase()

        if (nome.isBlank())
            return ResultadoAuth.Falha("Informe seu nome.", Campo.NOME)
        if (!regexEmail.matches(emailLimpo))
            return ResultadoAuth.Falha("Digite um e-mail válido.", Campo.EMAIL)
        if (senha.length < 6)
            return ResultadoAuth.Falha("A senha precisa ter 6 ou mais caracteres.", Campo.SENHA)
        if (senha != confirmarSenha)
            return ResultadoAuth.Falha("As senhas não são iguais.", Campo.CONFIRMAR_SENHA)
        if (usuarioDao.buscarPorEmail(emailLimpo) != null)
            return ResultadoAuth.Falha("Este e-mail já está cadastrado.", Campo.EMAIL)

        val id = try {
            usuarioDao.inserir(
                Usuario(nome = nome.trim(), email = emailLimpo, senhaHash = senha.sha256())
            )
        } catch (e: SQLiteConstraintException) {
            return ResultadoAuth.Falha("Este e-mail já está cadastrado.", Campo.EMAIL)
        }

        sessao.salvar(id) // já entra logado depois do cadastro
        return ResultadoAuth.Sucesso
    }

    suspend fun entrar(email: String, senha: String): ResultadoAuth {
        if (email.isBlank() || senha.isBlank())
            return ResultadoAuth.Falha("Preencha e-mail e senha.")

        val usuario = usuarioDao.buscarPorEmail(email.trim().lowercase())

        // Mesma mensagem para e-mail inexistente e senha errada:
        // não contamos qual dos dois campos está errado.
        if (usuario == null || usuario.senhaHash != senha.sha256())
            return ResultadoAuth.Falha("E-mail ou senha incorretos. Tente novamente.")

        sessao.salvar(usuario.id)
        return ResultadoAuth.Sucesso
    }

    fun sair() = sessao.limpar()
}
