package isel.daw.battleships.model


enum class Type(private val letter: Char) {
    CARRIER('c'),
    BATTLESHIP('b'),
    DESTROYER('d'),
    SUBMARINE('s'),
    PATROL_BOAT('p'),
    SEA('_'),
    HIT('x'),
    MISS('o');

    override fun toString(): String {
        return letter.toString()
    }
}

enum class Army {
    RED, BLUE;

    val other: Army
        get() = if (this == RED) BLUE else RED

    override fun toString(): String {
        return if (this == RED) "r" else "b"
    }
}

class Piece(var type: Type, val army: Army) {

    override fun equals(other: Any?) = if (other !is Piece) false else ((type == other.type) && (army == other.army))

    override fun toString() = if (this.army == Army.BLUE) this.type.toString().uppercase() else this.type.toString()
}

fun Char.toPiece(army: Army): Piece = this.toPieceOrNull(army) ?: throw IllegalArgumentException()

fun Char.toPieceOrNull(army: Army): Piece? = when (this.uppercaseChar()) {
    'C' -> if (army == Army.BLUE) Piece(Type.CARRIER, Army.BLUE) else Piece(Type.CARRIER, Army.RED)
    'B' -> if (army == Army.BLUE) Piece(Type.BATTLESHIP, Army.BLUE) else Piece(Type.BATTLESHIP, Army.RED)
    'D' -> if (army == Army.BLUE) Piece(Type.DESTROYER, Army.BLUE) else Piece(Type.DESTROYER, Army.RED)
    'S' -> if (army == Army.BLUE) Piece(Type.SUBMARINE, Army.BLUE) else Piece(Type.SUBMARINE, Army.RED)
    'P' -> if (army == Army.BLUE) Piece(Type.PATROL_BOAT, Army.BLUE) else Piece(Type.PATROL_BOAT, Army.RED)
    '_' -> if (army == Army.BLUE) Piece(Type.SEA, Army.BLUE) else Piece(Type.SEA, Army.RED)
    'X' -> if (army == Army.BLUE) Piece(Type.HIT, Army.BLUE) else Piece(Type.HIT, Army.RED)
    'O' -> if (army == Army.BLUE) Piece(Type.MISS, Army.BLUE) else Piece(Type.MISS, Army.RED)
    else -> null
}
