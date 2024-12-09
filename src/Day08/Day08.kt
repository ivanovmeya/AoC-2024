import kotlin.math.abs

fun main() {

    fun parse(input: List<String>): HashMap<Char, MutableList<Cell>> {
        val result = hashMapOf<Char, MutableList<Cell>>()

        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, ch ->
                if (ch != '.') {
                    result[ch] = result.getOrDefault(ch, mutableListOf()).apply {
                        add(Cell(row, col))
                    }
                }
            }

        }

        return result
    }

    fun inbounds(cell: Cell, input: List<String>): Boolean {
        return cell.r >= 0 && cell.r < input.size && cell.c >= 0 && cell.c < input[0].length
    }

    fun countAntinodes(
        cells: HashMap<Char, MutableList<Cell>>,
        input: List<String>,
        part1: Boolean,
        isSingleAntenaOrDot: (cell: Cell) -> Boolean
    ): HashSet<Cell> {
        val antinodes = hashSetOf<Cell>()
        val cap = if (part1) 1 else 100
        for (ch in cells.keys) {

            //same cells
            val sameCells = cells[ch]!!

            //we pair each cell with all others
            for (i in sameCells.indices) {
                val cell1 = sameCells[i]
                for (j in i + 1 until sameCells.size) {
                    val cell2 = sameCells[j]

                    var localCell1 = cell1
                    var localCell2 = cell2

                    //how to choose direction?
                    var newCell1: Cell = Cell(0, 0)
                    var newCell2: Cell = Cell(0, 0)

                    val rowD = abs(localCell1.r - localCell2.r)
                    val colD = abs(localCell1.c - localCell2.c)

                    var count = 0

                    while (count < cap && (inbounds(newCell1, input) || inbounds(newCell2, input))) {

                        val (row1, row2) = if (localCell1.r >= localCell2.r) {
                            localCell1.r + rowD to localCell2.r - rowD
                        } else {
                            localCell1.r - rowD to localCell2.r + rowD
                        }

                        val (col1, col2) = if (localCell1.c >= localCell2.c) {
                            localCell1.c + colD to localCell2.c - colD
                        } else {
                            localCell1.c - colD to localCell2.c + colD
                        }

                        newCell1 = Cell(row1, col1)
                        newCell2 = Cell(row2, col2)

                        if (inbounds(newCell1, input) && isSingleAntenaOrDot(newCell1)) antinodes.add(newCell1)
                        if (inbounds(newCell2, input) && isSingleAntenaOrDot(newCell2)) antinodes.add(newCell2)

                        localCell1 = newCell1
                        localCell2 = newCell2

                        count++
                    }

                }
            }
        }
        return antinodes
    }

    fun part1(input: List<String>): Int {
        val cap = 1
        val cells = parse(input)
        return countAntinodes(cells, input, true, {true}).size
    }

    fun part2(input: List<String>): Int {
        val cap = 1000
        val cells = parse(input)

        var antenasCount = 0
        for (i in input.indices) {
            for (j in input.indices) {
                val ch = input[i][j]
                if (ch != '.' && cells[ch]!!.size != 1) antenasCount++
            }
        }


        val antinodes = countAntinodes(cells, input, false) { cell ->
            val ch = input[cell.r][cell.c]
            ch == '.' || cells[ch]!!.size == 1
        }



        for (i in input.indices) {
            for (j in input[0].indices) {
                if (antinodes.contains(Cell(i,j))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }

        return antinodes.size + antenasCount
    }

    val testInput = readInput("Day08", true, 1)
    val part1 = part1(testInput)
    println("part1 = $part1")
    check(part1 == 14)

    val testInput2 = readInput("Day08", true, 2)
    val part2 = part2(testInput2)
    println("part2 = $part2")
    check(part2 == 34)

    val input = readInput("Day08", false)
    part1(input).println()
    part2(input).println()
}
