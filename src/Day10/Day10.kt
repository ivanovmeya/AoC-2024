fun main() {

    fun parse(input: List<String>): Pair<List<List<Int>>, List<Cell>> {

        val trailHeads = mutableListOf<Cell>()
        val map = input.mapIndexed { row, line ->
            line.toCharArray().mapIndexed { col, ch ->
                if (!ch.isDigit()) {
                    -1
                } else {
                    ch.digitToInt().also { digit ->
                        if (digit == 0) {
                            trailHeads.add(Cell(row, col))
                        }
                    }
                }
            }
        }

        return map to trailHeads

    }

    fun inbounds(map: List<List<Int>>, cell: Cell) =
        cell.r >= 0 && cell.c >= 0 && cell.r < map.size && cell.c < map[0].size

    fun score(map: List<List<Int>>, cell: Cell, prevHeight: Int, usedNines: HashSet<Cell>, part2: Boolean): Int {
        if (!inbounds(map, cell)) return 0
        val height = map[cell.r][cell.c]
        if (height != prevHeight + 1) return 0
        if (height == 9 && (if (part2) true else !usedNines.contains(cell))) {
            usedNines.add(cell)
            return 1
        }

        return score(map, cell.moveUp(), height, usedNines, part2) +
                score(map, cell.moveRight(), height, usedNines, part2) +
                score(map, cell.moveDown(), height, usedNines, part2) +
                score(map, cell.moveLeft(), height, usedNines, part2)
    }


    fun part1(input: List<String>): Int {
        val (map, trailheads) = parse(input)
        return trailheads.sumOf { head ->
            val usedNines = hashSetOf<Cell>()
            score(map, head, -1, usedNines, false)
        }
    }

    fun part2(input: List<String>): Int {
        val (map, trailheads) = parse(input)
        return trailheads.sumOf { head ->
            val usedNines = hashSetOf<Cell>()
            score(map, head, -1, usedNines, true)
        }
    }

    val testInput = readInput("Day10", true, 1)
    val part1 = part1(testInput)
    check(part1 == 36)

    val testInput2 = readInput("Day10", true, 2)
    check(part2(testInput2) == 81)

    val input = readInput("Day10", false)
    part1(input).println()
    part2(input).println()
}