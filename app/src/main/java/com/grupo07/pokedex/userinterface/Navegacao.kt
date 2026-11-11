package com.grupo07.pokedex.userinterface

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.grupo07.pokedex.userinterface.screens.CadastroScreen
import com.grupo07.pokedex.userinterface.screens.DetalheScreen
import com.grupo07.pokedex.userinterface.screens.FavoritosScreen
import com.grupo07.pokedex.userinterface.screens.LoginScreen
import com.grupo07.pokedex.userinterface.screens.PerfilScreen
import com.grupo07.pokedex.userinterface.screens.PokedexScreen
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.userinterface.theme.VermelhoClaro
import kotlinx.serialization.Serializable

// ---------- Rotas type-safe ----------
// Cada tela é uma classe @Serializable. A de detalhe carrega o id do Pokémon.

@Serializable object LoginRoute
@Serializable object CadastroRoute
@Serializable object PokedexRoute
@Serializable object FavoritosRoute
@Serializable object PerfilRoute
@Serializable data class DetalheRoute(val pokemonId: Int)

/** As 3 abas da barra inferior. */
private class Aba(
    val rota: Any,
    val titulo: String,
    val icone: ImageVector,
    val ehEsta: (NavDestination) -> Boolean
)

private val ABAS = listOf(
    Aba(PokedexRoute, "Pokedex", Icons.Filled.CatchingPokemon) { it.hasRoute<PokedexRoute>() },
    Aba(FavoritosRoute, "Favoritos", Icons.Filled.Favorite) { it.hasRoute<FavoritosRoute>() },
    Aba(PerfilRoute, "Perfil", Icons.Filled.Person) { it.hasRoute<PerfilRoute>() }
)

/** true se a tela atual é a desta aba. */
private fun NavDestination?.ehDa(aba: Aba): Boolean =
    this?.hierarchy?.any { aba.ehEsta(it) } == true

@Composable
fun AppNavegacao(estaLogado: Boolean) {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destinoAtual = entradaAtual?.destination

    // A barra inferior só aparece nas 3 abas (não no login, cadastro ou detalhe)
    val mostrarBarra = ABAS.any { destinoAtual.ehDa(it) }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) BarraInferior(navController, destinoAtual)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            // Só quem entrou vê o app: sem sessão, começa no login
            startDestination = if (estaLogado) PokedexRoute else LoginRoute,
            modifier = Modifier.padding(padding)
        ) {
            composable<LoginRoute> {
                LoginScreen(
                    onLoginOk = { navController.irParaApp() },
                    onIrParaCadastro = { navController.navigate(CadastroRoute) }
                )
            }
            composable<CadastroRoute> {
                CadastroScreen(
                    onCadastroOk = { navController.irParaApp() },
                    onVoltar = { navController.popBackStack() }
                )
            }
            composable<PokedexRoute> {
                PokedexScreen(onAbrirPokemon = { id -> navController.navigate(DetalheRoute(id)) })
            }
            composable<FavoritosRoute> {
                FavoritosScreen(
                    onAbrirPokemon = { id -> navController.navigate(DetalheRoute(id)) },
                    onExplorar = { navController.irParaAba(PokedexRoute) }
                )
            }
            composable<PerfilRoute> {
                PerfilScreen(onSair = {
                    // Sair: apaga a pilha inteira e volta para o login
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                })
            }
            composable<DetalheRoute> { entrada ->
                // toRoute lê o parâmetro type-safe da rota
                val rota = entrada.toRoute<DetalheRoute>()
                DetalheScreen(pokemonId = rota.pokemonId, onVoltar = { navController.popBackStack() })
            }
        }
    }
}

/** Depois do login/cadastro: abre a Pokedex e tira login/cadastro da pilha. */
private fun NavHostController.irParaApp() {
    navigate(PokedexRoute) {
        popUpTo(graph.id) { inclusive = true }
    }
}

/** Troca de aba sem empilhar várias cópias da mesma tela. */
private fun NavHostController.irParaAba(rota: Any) {
    navigate(rota) {
        popUpTo<PokedexRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BarraInferior(navController: NavHostController, destinoAtual: NavDestination?) {
    NavigationBar(containerColor = Color.White) {
        ABAS.forEach { aba ->
            NavigationBarItem(
                selected = destinoAtual.ehDa(aba),
                onClick = { navController.irParaAba(aba.rota) },
                icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                label = { Text(aba.titulo, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Vermelho,
                    selectedTextColor = Vermelho,
                    indicatorColor = VermelhoClaro,
                    unselectedIconColor = CinzaTexto,
                    unselectedTextColor = CinzaTexto
                )
            )
        }
    }
}
