package com.grupo07.pokedex.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabela de usuários. O e-mail é único (índice unique) e a senha
 * é guardada só como hash SHA-256, nunca em texto puro.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val senhaHash: String,
    val criadoEm: Long = System.currentTimeMillis()
)

/**
 * Favoritos de cada usuário. A chave é (usuarioId, pokemonId):
 * o mesmo Pokémon pode ser favorito de várias contas sem misturar.
 * Guardamos nome, imagem e tipos para a aba Favoritos funcionar offline.
 */
@Entity(
    tableName = "favoritos",
    primaryKeys = ["usuarioId", "pokemonId"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuarioId"])]
)
data class Favorito(
    val usuarioId: Long,
    val pokemonId: Int,
    val nome: String,
    val imagemUrl: String,
    val tipos: String,            // ex.: "grass,poison"
    val salvoEm: Long = System.currentTimeMillis()
) {
    val listaTipos: List<String> get() = tipos.split(",").filter { it.isNotBlank() }
}
