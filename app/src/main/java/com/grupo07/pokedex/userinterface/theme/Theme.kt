package com.grupo07.pokedex.userinterface.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cores do protótipo do Figma
val Vermelho = Color(0xFFE3192F)
val VermelhoClaro = Color(0xFFFDECEE)
val Fundo = Color(0xFFF6F6F6)
val CinzaTexto = Color(0xFF6B6B6B)
val CinzaBorda = Color(0xFFE2E2E2)
val CinzaEsqueleto = Color(0xFFE9E9E9)

private val Cores = lightColorScheme(
    primary = Vermelho,
    onPrimary = Color.White,
    primaryContainer = VermelhoClaro,
    onPrimaryContainer = Vermelho,
    secondary = Vermelho,
    background = Fundo,
    onBackground = Color(0xFF1C1C1C),
    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFF1F1F1),
    onSurfaceVariant = CinzaTexto,
    outline = CinzaBorda,
    error = Vermelho
)

@Composable
fun PokedexTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Cores, content = content)
}
