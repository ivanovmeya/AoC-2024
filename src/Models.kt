data class Cell(val r: Int, val c: Int) {
    fun moveUp() = Cell(r - 1, c)
    fun moveDown() = Cell(r + 1, c)
    fun moveLeft() = Cell(r, c - 1)
    fun moveRight() = Cell(r, c + 1)

    fun turnRight(): Cell {
        return when (this) {
            Cell(-1, 0) -> Cell(0, 1) //from UP to RIGHT
            Cell(0, 1) -> Cell(1, 0) //from RIGHT to DOWN
            Cell(1, 0) -> Cell(0, -1) //from DOWN to LEFT
            Cell(0, -1) -> Cell(-1, 0) //from LEFT to UP
            else -> throw IllegalStateException("Wrong direction")
        }
    }
}

data class Point(val x: Long, val y: Long)