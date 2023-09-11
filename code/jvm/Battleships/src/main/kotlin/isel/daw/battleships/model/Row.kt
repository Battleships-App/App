package isel.daw.battleships.model

enum class Row(val digit: Char) {
    // Do not change order
    R8('8'), R7('7'), R6('6'), R5('5'), R4('4'), R3('3'), R2('2'), R1('1');

    companion object {
        val values = values()
    }

    override fun toString() = digit.toString()
}

fun Char.toRowOrNull() = Row.values.elementAtOrNull(7 - (this - '1'))

fun Int.toRow() = Row.values.elementAt(this)

