package com.grupo07.pokedex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Banco Room do app. Se mudar uma Entity, aumente a versão
 * (ou desinstale o app do emulador), senão o app quebra ao abrir.
 */
@Database(
    entities = [Usuario::class, Favorito::class],
    version = 1,
    exportSchema = false
)
abstract class PokedexDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile
        private var INSTANCIA: PokedexDatabase? = null

        fun getInstance(context: Context): PokedexDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    PokedexDatabase::class.java,
                    "pokedex.db"
                ).build().also { INSTANCIA = it }
            }
    }
}
