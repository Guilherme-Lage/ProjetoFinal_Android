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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.grupo07.pokedex.data.PokemonDetalhe
import com.grupo07.pokedex.userinterface.components.EstadoErro
import com.grupo07.pokedex.userinterface.components.TipoChip
import com.grupo07.pokedex.userinterface.components.infoTipo
import com.grupo07.pokedex.userinterface.components.nomeAtributo
import com.grupo07.pokedex.userinterface.components.numeroPokedex
import com.grupo07.pokedex.userinterface.theme.CinzaBorda
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.userinterface.theme.VermelhoClaro
import com.grupo07.pokedex.viewmodel.DetalheEstado
import com.grupo07.pokedex.viewmodel.DetalheViewModel
import java.util.Locale

/**
 * Recebe o id pela rota type-safe DetalheRoute(pokemonId) e pede ao ViewModel para carregar.
 */
@Composable
fun DetalheScreen(
    pokemonId: Int,
    onVoltar: () -> Unit,
    viewModel: DetalheViewModel = viewModel()
) {
    LaunchedEffect(pokemonId) { viewModel.carregar(pokemonId) }

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val favorito by viewModel.ehFavorito.collectAsStateWithLifecycle()

    when (val e = estado) {
        is DetalheEstado.Carregando -> Column(Modifier.fillMaxSize()) {
            BotaoVoltar(onVoltar, Color.Black)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DetalheEstado.Erro -> Column(Modifier.fillMaxSize()) {
            BotaoVoltar(onVoltar, Color.Black)
            EstadoErro(semConexao = e.semConexao, onTentarDeNovo = viewModel::tentarDeNovo)
        }

        is DetalheEstado.Sucesso -> ConteudoDetalhe(
            pokemon = e.pokemon,
            favorito = favorito,
            onVoltar = onVoltar,
            onFavoritar = viewModel::alternarFavorito
        )
    }
}

@Composable
private fun BotaoVoltar(onVoltar: () -> Unit, cor: Color) {
    IconButton(onClick = onVoltar) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = cor)
    }
}

@Composable
private fun ConteudoDetalhe(
    pokemon: PokemonDetalhe,
    favorito: Boolean,
    onVoltar: () -> Unit,
    onFavoritar: () -> Unit
) {
    val corTipo = infoTipo(pokemon.tipos.firstOrNull() ?: "normal").cor
    val ptBr = Locale.forLanguageTag("pt-BR")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Cabeçalho colorido pela cor do tipo principal
        Box(
            Modifier
                .fillMaxWidth()
                .height(270.dp)
                .background(corTipo, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BotaoVoltar(onVoltar, Color.White)
                Spacer(Modifier.weight(1f))
                Text(
                    numeroPokedex(pokemon.id),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            Box(
                Modifier
                    .align(Alignment.Center)
                    .padding(top = 24.dp)
                    .size(190.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.imagemUrl,
                    contentDescription = pokemon.nome,
                    modifier = Modifier.size(170.dp)
                )
            }
        }

        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(pokemon.nome, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        pokemon.tipos.forEach { TipoChip(it) }
                    }
                }
                IconButton(
                    onClick = onFavoritar,
                    modifier = Modifier.background(VermelhoClaro, CircleShape)
                ) {
                    Icon(
                        if (favorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (favorito) "Remover dos favoritos" else "Adicionar aos favoritos",
                        tint = Vermelho
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Info(String.format(ptBr, "%.1f m", pokemon.alturaM), "Altura", Modifier.weight(1f))
                Info(String.format(ptBr, "%.1f kg", pokemon.pesoKg), "Peso", Modifier.weight(1f))
                Info(pokemon.xpBase.toString(), "XP base", Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))
            Text("Atributos base", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            pokemon.atributos.forEach { atributo ->
                Row(
                    Modifier.padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(nomeAtributo(atributo.nome), color = CinzaTexto, modifier = Modifier.width(110.dp))
                    Text(atributo.valor.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                    LinearProgressIndicator(
                        progress = { (atributo.valor / 255f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp),
                        color = corTipo,
                        trackColor = CinzaBorda
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            if (favorito) {
                OutlinedButton(
                    onClick = onFavoritar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Vermelho)
                ) { Text("Remover dos Favoritos", fontWeight = FontWeight.SemiBold) }
            } else {
                Button(
                    onClick = onFavoritar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50)
                ) { Text("Adicionar aos Favoritos", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun Info(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontWeight = FontWeight.Bold)
        Text(rotulo, color = CinzaTexto, style = MaterialTheme.typography.labelSmall)
    }
}
