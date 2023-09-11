package com.isel.battleshipsAndroid.utils

data class PlayerDTO(
    val username: String,
    val games: Int,
    val points: Int
)

data class GameDTO(
    val uuid: String,
    val state: String,
    val player1: String,
    val player2: String,
    val board: String,
    val moves1: String,
    val moves2: String,
    val spr: Int,
    val result: Int
)