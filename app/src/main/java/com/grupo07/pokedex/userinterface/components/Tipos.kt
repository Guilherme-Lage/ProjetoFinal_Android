package com.grupo07.pokedex.userinterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Nome em português e cor de cada tipo (a API devolve em inglês). */
data class InfoTipo(val nome: String, val cor: Color)

val TIPOS: Map<String, InfoTipo> = linkedMapOf(
    "normal" to InfoTipo("Normal", Color(0xFFA8A77A)),
    "fire" to InfoTipo("Fogo", Color(0xFFEE8130)),
    "water" to InfoTipo("Água", Color(0xFF6390F0)),
    "grass" to InfoTipo("Planta", Color(0xFF4CAF50)),
    "electric" to InfoTipo("Elétrico", Color(0xFFE6B800)),
    "ice" to InfoTipo("Gelo", Color(0xFF6FC8C4)),
    "fighting" to InfoTipo("Lutador", Color(0xFFC22E28)),
    "poison" to InfoTipo("Venenoso", Color(0xFFA33EA1)),
    "ground" to InfoTipo("Terra", Color(0xFFC9A54B)),
    "flying" to InfoTipo("Voador", Color(0xFF8F7BE0)),
    "psychic" to InfoTipo("Psíquico", Color(0xFFF95587)),
    "bug" to InfoTipo("Inseto", Color(0xFF8FA31A)),
    "rock" to InfoTipo("Pedra", Color(0xFFB6A136)),
    "ghost" to InfoTipo("Fantasma", Color(0xFF735797)),
    "dragon" to InfoTipo("Dragão", Color(0xFF6F35FC)),
    "dark" to InfoTipo("Sombrio", Color(0xFF705746)),
    "steel" to InfoTipo("Aço", Color(0xFF8E8EAE)),
    "fairy" to InfoTipo("Fada", Color(0xFFD685AD))
)

fun infoTipo(tipo: String): InfoTipo =
    TIPOS[tipo] ?: InfoTipo(tipo.replaceFirstChar { it.uppercase() }, Color.Gray)

/** Nome dos atributos base em português. */
fun nomeAtributo(nome: String): String = when (nome) {
    "hp" -> "HP"
    "attack" -> "Ataque"
    "defense" -> "Defesa"
    "special-attack" -> "Atq. Esp."
    "special-defense" -> "Def. Esp."
    "speed" -> "Velocidade"
    else -> nome
}

/** "Etiqueta" colorida com o nome do tipo. */
@Composable
fun TipoChip(tipo: String, modifier: Modifier = Modifier) {
    val info = infoTipo(tipo)
    Text(
        text = info.nome,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(info.cor, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/** 1 -> "#001" */
fun numeroPokedex(id: Int): String = "#" + id.toString().padStart(3, '0')
