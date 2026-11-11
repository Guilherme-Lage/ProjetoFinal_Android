package com.grupo07.pokedex.userinterface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.saveable.rememberSaveable
import com.grupo07.pokedex.userinterface.theme.PokedexTheme
import com.grupo07.pokedex.viewmodel.SessaoViewModel

class MainActivity : ComponentActivity() {

    private val sessaoViewModel: SessaoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // rememberSaveable: ao girar a tela, a tela inicial continua a mesma
            val logadoNaAbertura = rememberSaveable { sessaoViewModel.estaLogado }
            PokedexTheme {
                AppNavegacao(estaLogado = logadoNaAbertura)
            }
        }
    }
}
