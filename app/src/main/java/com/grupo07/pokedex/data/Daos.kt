package com.grupo07.pokedex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    /** ABORT: se o e-mail já existir, o índice unique faz o insert falhar. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observarPorId(id: Long): Flow<Usuario?>
}

@Dao
interface FavoritoDao {

    /** Flow: a tela atualiza sozinha quando um favorito entra ou sai. */
    @Query("SELECT * FROM favoritos WHERE usuarioId = :usuarioId ORDER BY pokemonId")
    fun listar(usuarioId: Long): Flow<List<Favorito>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE usuarioId = :usuarioId AND pokemonId = :pokemonId)")
    fun ehFavorito(usuarioId: Long, pokemonId: Int): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM favoritos WHERE usuarioId = :usuarioId")
    fun contar(usuarioId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(favorito: Favorito)

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND pokemonId = :pokemonId")
    suspend fun remover(usuarioId: Long, pokemonId: Int)
}
