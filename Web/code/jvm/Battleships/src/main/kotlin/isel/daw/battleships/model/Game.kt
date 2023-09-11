package isel.daw.battleships.model


data class Game(
    val uuid: String,
    val player1Username: String,
    var player2Username: String = "",
    var gameState: State = State.WAITING,
    var nextPlay: Turn = Turn.PLAYER2,
    var board1: String = "",
    var board2: String = "",
    var moves1: String = "",
    var moves2: String = "",
    val shotsPerRound: Int = 1,
    var result: Int = 0
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
    val result: Int
)

enum class State {
    WAITING,
    BUILDING,
    STARTED,
    ENDED;
}

/**
 * Class que permite ser alterada após uma jogada, para permitir que o jogador saiba
 * quando é que pode jogar
 */
enum class Turn {
    PLAYER1, PLAYER2
}

