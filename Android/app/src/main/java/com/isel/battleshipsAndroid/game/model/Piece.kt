package com.isel.battleshipsAndroid.game.model

import androidx.compose.ui.graphics.Color


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

    fun toColor(enemyBoard: Boolean): Color = when (letter.uppercaseChar()) {
        'C' -> if (enemyBoard) Color(0xff3d86e4)  else Color(0xff2f2c2c)
        'B' -> if (enemyBoard) Color(0xff3d86e4)  else Color(0xff2f2c2c)
        'D' -> if (enemyBoard) Color(0xff3d86e4)  else Color(0xff2f2c2c)
        'S' -> if (enemyBoard) Color(0xff3d86e4)  else Color(0xff2f2c2c)
        'P' -> if (enemyBoard) Color(0xff3d86e4)  else Color(0xff2f2c2c)
        '_' -> Color(0xff3d86e4)
        'X' -> Color(0xfff40000)
        'O' -> Color(0xff2e00b8)
        else -> Color.LightGray
    }
}

class Piece(var type: Type) {

    override fun equals(other: Any?) = if (other !is Piece) false else (type == other.type)

    override fun toString() = this.type.toString().uppercase()
}

fun Char.toPiece(): Piece = this.toPieceOrNull() ?: throw IllegalArgumentException()

fun Char.toPieceOrNull(): Piece? = when (this.uppercaseChar()) {
    'C' -> Piece(Type.CARRIER)
    'B' -> Piece(Type.BATTLESHIP)
    'D' -> Piece(Type.DESTROYER)
    'S' -> Piece(Type.SUBMARINE)
    'P' -> Piece(Type.PATROL_BOAT)
    '_' -> Piece(Type.SEA)
    'X' -> Piece(Type.HIT)
    'O' -> Piece(Type.MISS)
    else -> null
}
