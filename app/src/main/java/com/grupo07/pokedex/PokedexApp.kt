package com.grupo07.pokedex

import android.app.Application
import com.grupo07.pokedex.data.ServiceLocator

/**
 * Classe Application: é criada uma vez quando o app abre, antes de qualquer tela.
 * Aqui só entregamos o Context para o ServiceLocator montar banco, sessão e repositórios.
 */
class PokedexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
