package com.grupo07.pokedex.data

import java.security.MessageDigest

/** Transforma a senha em um hash SHA-256 (64 caracteres hexadecimais). */
fun String.sha256(): String =
    MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }
