package com.grupo07.pokedex.userinterface.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupo07.pokedex.userinterface.theme.CinzaBorda
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.viewmodel.PerfilViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PerfilScreen(
    onSair: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsStateWithLifecycle()
    val total by viewModel.totalFavoritos.collectAsStateWithLifecycle()

    val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    Column(Modifier.fillMaxSize()) {
        CabecalhoAba(titulo = "Perfil")

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .size(84.dp)
                    .background(Vermelho, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    usuario?.nome?.firstOrNull()?.uppercase() ?: "",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(usuario?.nome ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(usuario?.email ?: "", color = CinzaTexto, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LinhaPerfil("Favoritos salvos", total.toString())
                HorizontalDivider(color = CinzaBorda)
                LinhaPerfil("Membro desde", usuario?.let { formatoData.format(Date(it.criadoEm)) } ?: "")
                HorizontalDivider(color = CinzaBorda)
                LinhaPerfil("Versão do app", "1.0")
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    viewModel.sair() // limpa a sessão salva
                    onSair()         // volta para o login
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Vermelho),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Vermelho)
            ) { Text("Sair da conta", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun LinhaPerfil(rotulo: String, valor: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(rotulo, modifier = Modifier.weight(1f))
        Text(valor, color = CinzaTexto)
    }
}
