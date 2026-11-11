package com.grupo07.pokedex.userinterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.grupo07.pokedex.data.Favorito
import com.grupo07.pokedex.userinterface.components.IconeCirculo
import com.grupo07.pokedex.userinterface.components.TipoChip
import com.grupo07.pokedex.userinterface.components.numeroPokedex
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.viewmodel.FavoritosViewModel

@Composable
fun FavoritosScreen(
    onAbrirPokemon: (Int) -> Unit,
    onExplorar: () -> Unit,
    viewModel: FavoritosViewModel = viewModel()
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        val lista = favoritos
        CabecalhoAba(
            titulo = "Favoritos",
            direita = lista?.let { if (it.size == 1) "1 salvo" else "${it.size} salvos" }
        )

        when {
            lista == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            lista.isEmpty() -> FavoritosVazio(onExplorar)

            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lista, key = { it.pokemonId }) { fav ->
                    ItemFavorito(
                        favorito = fav,
                        onClick = { onAbrirPokemon(fav.pokemonId) },
                        onRemover = { viewModel.remover(fav.pokemonId) }
                    )
                }
                item {
                    Text(
                        "Toque em um Pokémon para ver os detalhes",
                        color = CinzaTexto,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemFavorito(favorito: Favorito, onClick: () -> Unit, onRemover: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = favorito.imagemUrl,
                contentDescription = favorito.nome,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(numeroPokedex(favorito.pokemonId), color = CinzaTexto, style = MaterialTheme.typography.labelSmall)
                Text(favorito.nome, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    favorito.listaTipos.forEach { TipoChip(it) }
                }
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Remover ${favorito.nome}", tint = CinzaTexto)
            }
        }
    }
}

@Composable
private fun FavoritosVazio(onExplorar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconeCirculo(Icons.Filled.FavoriteBorder)
        Spacer(Modifier.height(16.dp))
        Text("Nenhum favorito ainda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            "Abra um Pokémon e toque no coração para salvá-lo aqui.",
            color = CinzaTexto,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = onExplorar,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Vermelho)
        ) { Text("Explorar Pokémon") }
    }
}
