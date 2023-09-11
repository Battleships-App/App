package isel.daw.battleships.model

class Board : Comparable<Board> {

    private val gameId: String
    val board: Array<Array<Piece>>

    constructor(type: Army) {
        this.gameId = "-1"
        this.board = newBoard(type)
    }

    constructor(gameId: String, type: Army, boardString: String) {
        this.gameId = gameId
        this.board = boardString.toBoard(type)
    }

    private fun newBoard(type: Army): Array<Array<Piece>> {
        return arrayOf(
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
            Array(8) { Piece(Type.SEA, type) },
        )
    }

    override fun toString() = board.joinToString("") { row ->
        row.joinToString("") {
            it.toString()
        }
    }

    override fun compareTo(other: Board): Int {
        var myBoardPiece: Piece
        var otherBoardPiece: Piece
        for (x in 0..7) {
            for (y in 0..7) {
                myBoardPiece = board[x][y]
                otherBoardPiece = other.board[x][y]
                if (myBoardPiece.type != otherBoardPiece.type && myBoardPiece.army != otherBoardPiece.army) return -1
            }
        }
        return 0
    }
}

fun String.toBoard(type: Army): Array<Array<Piece>> {
    val board = Board(type).board
    var idx = 0
    for (x in 0..7) {
        for (y in 0..7) {
            if (idx < this.length) {
                board[x][y] = this[idx++].toPiece(type)
            }
        }
    }
    return board
}

fun String.toBoardObject(type: Army): Board {
    return Board("-1", type, this)
}
