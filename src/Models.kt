
data class Cell(val r: Int, val c: Int) {
    fun moveUp() = Cell(r - 1, c)
    fun moveDown() = Cell(r + 1, c)
    fun moveLeft() = Cell(r, c - 1)
    fun moveRight() = Cell(r, c + 1)
}