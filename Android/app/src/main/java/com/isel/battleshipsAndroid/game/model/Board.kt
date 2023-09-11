package com.isel.battleshipsAndroid.game.model

const val BOARD_ROWS = 8
private const val BOARD_COLUMNS = 8
private const val BOARD_SIZE = BOARD_COLUMNS * BOARD_ROWS


class Board {
    var board: Array<Piece>

    constructor() {
        this.board = Array(BOARD_SIZE) { Piece(Type.SEA) }
    }

    constructor(boardString: String) {
        this.board = boardString.toBoard()
    }

    private fun String.toBoard(): Array<Piece> {
        val board = Board().board
        val chunkedBoard = this.chunked(BOARD_ROWS).map{ it.reversed() }

        for (i in 0 until BOARD_ROWS){
            for (j in 0 until BOARD_COLUMNS) {
                board[i * BOARD_ROWS + j] = chunkedBoard[i][j].toPiece()
            }
        }

        return board
    }

}

class Ship (val name: String, var position: String = "a1", var orientation: String = "horizontal", var length: Int)

fun indexToCoordinates (index: Int): String {
    val res = (index % 8 + 1).toString()
    return when (index) {
        in 0 until 8 -> "a$res"
        in 8 until 16 -> "b$res"
        in 16 until 24 -> "c$res"
        in 24 until 32 -> "d$res"
        in 32 until 40 -> "e$res"
        in 40 until 48 -> "f$res"
        in 48 until 56 -> "g$res"
        in 56 until 64 -> "h$res"
        else -> {
            println("error mapping letter")
            "a1"
        }
    }
}

fun coordinatesToIndex (coordinates: String): Int? {
    val sec = coordinates[1].digitToInt() - 1
    return when(coordinates[0]) {
        'a' -> sec
        'b' -> BOARD_ROWS + sec
        'c' -> 2 * BOARD_ROWS + sec
        'd' -> 3 * BOARD_ROWS + sec
        'e' -> 4 * BOARD_ROWS + sec
        'f' -> 5 * BOARD_ROWS + sec
        'g' -> 6 * BOARD_ROWS + sec
        'h' -> 7 * BOARD_ROWS + sec
        else -> {
            println("error mapping letter")
            null
        }
    }
}

fun getIndexes(ship: Ship): ArrayList<Int> {
    val indices: ArrayList<Int> = ArrayList()
    var index = coordinatesToIndex(ship.position)!! // first index

    for (i in 0 until ship.length) {
        indices.add(index)
        index = if(ship.orientation == "vertical")  index + BOARD_ROWS else index + 1
    }

    return indices
}
