package com.isel.battleshipsAndroid.game.domain

import androidx.compose.ui.graphics.Color
import com.isel.battleshipsAndroid.game.model.*
import org.junit.Assert.*
import org.junit.Test


class BoardTests {

    @Test
    fun `newly created Board has all positions with Piece Sea`(){
        val board = Board()
        assertTrue(board.board.all { it.type==Type.SEA })
    }

    @Test
    fun testToColorForEachTypeWhenEnemyBoardIsTrue() {
        // Test Type.CARRIER
        assertEquals(Type.CARRIER.toColor(true), Color(0xff3d86e4))

        // Test Type.BATTLESHIP
        assertEquals(Type.BATTLESHIP.toColor(true), Color(0xff3d86e4))

        // Test Type.DESTROYER
        assertEquals(Type.DESTROYER.toColor(true), Color(0xff3d86e4))

        // Test Type.SUBMARINE
        assertEquals(Type.SUBMARINE.toColor(true), Color(0xff3d86e4))

        // Test Type.PATROL_BOAT
        assertEquals(Type.PATROL_BOAT.toColor(true), Color(0xff3d86e4))

        // Test Type.SEA
        assertEquals(Type.SEA.toColor(true), Color(0xff3d86e4))

        // Test Type.HIT
        assertEquals(Type.HIT.toColor(true), Color(0xfff40000))

        // Test Type.MISS
        assertEquals(Type.MISS.toColor(true), Color(0xff2e00b8))
    }

    @Test
    fun testToColorForEachTypeWhenEnemyBoardIsFalse() {
        // Test Type.CARRIER
        assertEquals(Type.CARRIER.toColor(false), Color(0xff2f2c2c))

        // Test Type.BATTLESHIP
        assertEquals(Type.BATTLESHIP.toColor(false), Color(0xff2f2c2c))

        // Test Type.DESTROYER
        assertEquals(Type.DESTROYER.toColor(false), Color(0xff2f2c2c))

        // Test Type.SUBMARINE
        assertEquals(Type.SUBMARINE.toColor(false), Color(0xff2f2c2c))

        // Test Type.PATROL_BOAT
        assertEquals(Type.PATROL_BOAT.toColor(false), Color(0xff2f2c2c))

        // Test Type.SEA
        assertEquals(Type.SEA.toColor(false), Color(0xff3d86e4))

        // Test Type.HIT
        assertEquals(Type.HIT.toColor(false), Color(0xfff40000))

        // Test Type.MISS
        assertEquals(Type.MISS.toColor(false), Color(0xff2e00b8))
    }

    @Test
    fun testEqualsForSameType() {
        val piece1 = Piece(Type.CARRIER)
        val piece2 = Piece(Type.CARRIER)
        assertTrue(piece1 == piece2)
    }

    @Test
    fun testEqualsForDifferentTypes() {
        val piece1 = Piece(Type.CARRIER)
        val piece2 = Piece(Type.BATTLESHIP)
        assertFalse(piece1 == piece2)
    }

    @Test
    fun testToString() {
        val piece = Piece(Type.CARRIER)
        assertEquals(piece.toString(), "C")
    }

    @Test
    fun testToPiece() {
        val piece = 'C'.toPiece()
        assertEquals(piece.type, Type.CARRIER)
    }

    @Test
    fun testToPieceOrNull() {
        val piece = 'C'.toPieceOrNull()
        assertEquals(piece?.type, Type.CARRIER)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testToPieceWithInvalidChar() {
        val piece = 'Z'.toPiece()
    }

    @Test
    fun testToPieceOrNullWithInvalidChar() {
        val piece = 'Z'.toPieceOrNull()
        assertNull(piece)
    }
}

