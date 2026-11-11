package com.grupo07.pokedex.userinterface.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupo07.pokedex.data.Campo
import com.grupo07.pokedex.userinterface.components.BannerErro
import com.grupo07.pokedex.userinterface.components.CampoTexto
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.viewmodel.CadastroViewModel

@Composable
fun CadastroScreen(
    onCadastroOk: () -> Unit,
    onVoltar: () -> Unit,
    viewModel: CadastroViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.sucesso) {
        if (estado.sucesso) {
            viewModel.sucessoTratado()
            onCadastroOk()
        }
    }

    // Mostra o erro embaixo do campo certo; se não tiver campo, mostra no topo
    fun erroDo(campo: Campo): String? = if (estado.campoComErro == campo) estado.erro else null

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        IconButton(onClick = onVoltar) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
        }
        Text("Criar conta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Seus favoritos ficam salvos na sua conta",
            color = CinzaTexto,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(20.dp))

        if (estado.erro != null && estado.campoComErro == null) {
            BannerErro(estado.erro!!)
            Spacer(Modifier.height(12.dp))
        }

        CampoTexto("Nome", estado.nome, viewModel::onNomeChange,
            placeholder = "Seu nome", erro = erroDo(Campo.NOME))
        Spacer(Modifier.height(8.dp))
        CampoTexto("E-mail", estado.email, viewModel::onEmailChange,
            placeholder = "seuemail@exemplo.com", erro = erroDo(Campo.EMAIL),
            tipoTeclado = KeyboardType.Email)
        Spacer(Modifier.height(8.dp))
        CampoTexto("Senha", estado.senha, viewModel::onSenhaChange,
            placeholder = "••••••", erro = erroDo(Campo.SENHA),
            ajuda = "Mínimo de 6 caracteres", senha = true)
        Spacer(Modifier.height(8.dp))
        CampoTexto("Confirmar senha", estado.confirmarSenha, viewModel::onConfirmarChange,
            placeholder = "••••••", erro = erroDo(Campo.CONFIRMAR_SENHA),
            senha = true, acaoTeclado = ImeAction.Done)
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = viewModel::cadastrar,
            enabled = !estado.carregando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            if (estado.carregando) {
                CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Cadastrar", fontWeight = FontWeight.SemiBold)
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Já tem conta?", color = CinzaTexto, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onVoltar) { Text("Entrar", fontWeight = FontWeight.Bold) }
        }
    }
}
