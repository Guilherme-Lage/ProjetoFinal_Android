package com.grupo07.pokedex.userinterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.grupo07.pokedex.userinterface.components.BannerErro
import com.grupo07.pokedex.userinterface.components.CampoTexto
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit,
    onIrParaCadastro: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Quando o login dá certo, avisa a navegação uma única vez
    LaunchedEffect(estado.sucesso) {
        if (estado.sucesso) {
            viewModel.sucessoTratado()
            onLoginOk()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoPokedex()
        Spacer(Modifier.height(12.dp))
        Text("Pokedex", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Entre para acessar sua conta", color = CinzaTexto, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(28.dp))

        estado.erro?.let {
            BannerErro(it)
            Spacer(Modifier.height(16.dp))
        }

        CampoTexto(
            rotulo = "E-mail",
            valor = estado.email,
            onValorChange = viewModel::onEmailChange,
            placeholder = "seuemail@exemplo.com",
            temErro = estado.erro != null,
            tipoTeclado = KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))
        CampoTexto(
            rotulo = "Senha",
            valor = estado.senha,
            onValorChange = viewModel::onSenhaChange,
            placeholder = "••••••",
            temErro = estado.erro != null,
            senha = true,
            acaoTeclado = ImeAction.Done
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::entrar,
            enabled = !estado.carregando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            if (estado.carregando) {
                CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Entrar", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text("Ainda não tem conta?", color = CinzaTexto, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onIrParaCadastro) {
                Text("Cadastre-se", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Quadrado vermelho com a Pokébola, igual ao Figma. */
@Composable
fun LogoPokedex() {
    Box(
        Modifier
            .size(72.dp)
            .background(Vermelho, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.CatchingPokemon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}
