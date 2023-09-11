package isel.daw.battleships.model

//jogada
data class Square(val col: Column, val row: Row, var hitOrMiss: Char? = null) {


    override fun equals(other: Any?): Boolean {
        if (other is Square)
            return this.col.ordinal == other.col.ordinal && this.row.ordinal == other.row.ordinal
        return false
    }

    override fun toString() = "$col$row$hitOrMiss"

    fun toIndexAt(): Int {
        val multiplier = this.col.ordinal
        val index = this.row.ordinal
        return index + (multiplier * 8)
    }
}

fun String.toSquare(): Square { //Conversão da String retirada da DB para Square
    val col = this.elementAt(0).toColumnOrNull() ?: throw IllegalArgumentException("Invalid syntax")
    val row = this.elementAt(1).toRowOrNull() ?: throw IllegalArgumentException("Invalid syntax")
    val hitOrMiss = if(this.elementAt(2)!='X' && this.elementAt(2)!='O') null else this.elementAt(2)
    return Square(col, row,hitOrMiss)
}

fun String.toSquareWithoutHitOrMiss() : Square {
    val col = this.elementAt(0).toColumnOrNull() ?: throw IllegalArgumentException("Invalid syntax")
    val row = this.elementAt(1).toRowOrNull() ?: throw IllegalArgumentException("Invalid syntax")
    return Square(col, row)
}


