package com.isel.battleshipsAndroid.game.model

data class Game(
    val uuid: String,
    val player1Username: String,
    var player2Username: String = "",
    var gameState: State = State("WAITING"),
    var nextPlay: Turn = Turn.PLAYER2,
    var board1: String = "",
    var board2: String = "",
    var moves1: String = "",
    var moves2: String = "",
    val shotsPerRound: Int = 1,
    var result: Int = 0,
)

data class GameOpenDTO(val uuid: String, val player1: String, val spr: Int)
data class GameEndedDTO(
    val uuid: String,
    val player1: String,
    val player2: String,
    val result: Int,
)

data class GameDTO(
    val uuid: String,
    val state: String,
    val nextPlay: String,
    val player1: String,
    val player2: String,
    val board: String,
    val moves1: String,
    val moves2: String,
    val spr: Int,
    val result: Int,
)

data class State (val status: String) // { WAITING, BUILDING, STARTED, ENDED }

/**
 * Class que permite ser alterada após uma jogada, para permitir que o jogador saiba
 * quando é que pode jogar
 */
enum class Turn {
    PLAYER1, PLAYER2
}

data class Room(val uuid: String, val player1: String, val spr: Int)

data class GameIdModel(val gameId: String)

data class GameBoard(val board: String)

data class GameLastState(val gameState: String, val turn :String, var board1: String, var board2: String, val moves1: String, val moves2:String, val result: Int)

data class HitOrMiss(val hitOrMiss : List<Boolean>)

data class FleetStatus( val fleetStatus : List<String>)