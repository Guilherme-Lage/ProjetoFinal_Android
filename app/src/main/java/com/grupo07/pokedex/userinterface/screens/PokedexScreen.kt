package com.grupo07.pokedex.userinterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.grupo07.pokedex.data.PokemonResumo
import com.grupo07.pokedex.userinterface.components.EstadoErro
import com.grupo07.pokedex.userinterface.components.TIPOS
import com.grupo07.pokedex.userinterface.components.numeroPokedex
import com.grupo07.pokedex.userinterface.theme.CinzaEsqueleto
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.viewmodel.ListaEstado
import com.grupo07.pokedex.viewmodel.PokedexViewModel

// Tipos que aparecem como filtro (os mais comuns na 1ª geração)
private val TIPOS_FILTRO = listOf("fire", "water", "grass", "electric", "poison", "psychic", "normal", "bug")

@Composable
fun PokedexScreen(
    onAbrirPokemon: (Int) -> Unit,
    viewModel: PokedexViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val nome by viewModel.primeiroNome.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        CabecalhoAba(titulo = "Pokedex", direita = if (nome.isNotBlank()) "Olá, $nome" else null)

        OutlinedTextField(
            value = estado.busca,
            onValueChange = viewModel::onBuscaChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar por nome ou número") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ChipFiltro("Todos", estado.tipoSelecionado == null) { viewModel.selecionarTipo(null) }
            }
            items(TIPOS_FILTRO) { tipo ->
                ChipFiltro(TIPOS.getValue(tipo).nome, estado.tipoSelecionado == tipo) {
                    viewModel.selecionarTipo(tipo)
                }
            }
        }

        when (val lista = estado.estado) {
            is ListaEstado.Carregando -> ListaCarregando()

            is ListaEstado.Erro -> EstadoErro(
                semConexao = lista.semConexao,
                onTentarDeNovo = viewModel::carregar,
                rodape = "Seus favoritos continuam disponíveis offline"
            )

            is ListaEstado.Sucesso ->
                if (lista.pokemons.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhum Pokémon encontrado", color = CinzaTexto)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista.pokemons, key = { it.id }) { pokemon ->
                            PokemonCard(pokemon) { onAbrirPokemon(pokemon.id) }
                        }
                    }
                }
        }
    }
}

/** Título da aba à esquerda e um texto opcional à direita. */
@Composable
fun CabecalhoAba(titulo: String, direita: String? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (direita != null) {
            Text(direita, color = CinzaTexto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selecionado,
        onClick = onClick,
        label = { Text(texto) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = Vermelho,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun PokemonCard(pokemon: PokemonResumo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(numeroPokedex(pokemon.id), color = CinzaTexto, style = MaterialTheme.typography.labelSmall)
            AsyncImage(
                model = pokemon.imagemUrl,
                contentDescription = pokemon.nome,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
                    .padding(vertical = 4.dp)
            )
            Text(pokemon.nome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        }
    }
}

/** Indicador + "esqueleto" da lista enquanto a API responde. */
@Composable
private fun ListaCarregando() {
    Column(Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("Carregando Pokémon...", color = CinzaTexto, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(12.dp))
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(150.dp)
                            .background(CinzaEsqueleto, RoundedCornerShape(14.dp))
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
