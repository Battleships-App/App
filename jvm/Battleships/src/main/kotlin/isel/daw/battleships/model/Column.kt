package isel.daw.battleships.model

enum class Column(val letter: Char) {
    // Do not change order
    A('a'), B('b'), C('c'), D('d'), E('e'), F('f'), G('g'), H('h');

    companion object {
        val values = values()
    }

    override fun toString(): String {
        return letter.toString()
    }
}

fun Char.toColumnOrNull() = Column.values.elementAtOrNull(this - 'a')

fun Int.toColumn() = Column.values.elementAt(this)
