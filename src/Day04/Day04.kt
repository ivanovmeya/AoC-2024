fun main() {

    fun countWords(monitor: List<String>, word: String): Int {
        val rows = monitor.size
        val cols = monitor[0].length
        val path = mutableSetOf<Pair<Int, Int>>()

        val globalPaths = HashSet<Pair<Int, Int>>()

        fun dfs(r: Int, c: Int, i: Int, d: Direction): Boolean {
            if (i == word.length) {
                globalPaths.addAll(path)
                return true
            }
            if (r < 0 || c < 0 || r >= rows || c >= cols ||
                monitor[r][c] != word[i] || Pair(r, c) in path
            ) {
                return false
            }

            path.add(Pair(r, c))
            val res = when (d) {
                Direction.HORIZONTAL -> dfs(r, c + 1, i + 1, d)
                Direction.VERTICAL -> dfs(r + 1, c, i + 1, d)
                Direction.LEFT_RIGHT_DIAGONAL -> dfs(r - 1, c - 1, i + 1, d)
                Direction.RIGHT_LEFT_DIAGONAL -> dfs(r - 1, c + 1, i + 1, d)
            }
            path.remove(Pair(r, c))

            return res
        }

        var count = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (dfs(r, c, 0, Direction.HORIZONTAL)) count++
                if (dfs(r, c, 0, Direction.VERTICAL)) count++
                if (dfs(r, c, 0, Direction.LEFT_RIGHT_DIAGONAL)) count++
                if (dfs(r, c, 0, Direction.RIGHT_LEFT_DIAGONAL)) count++
            }
        }

        return count
    }

    fun part1(input: List<String>): Int {
        val backwardsCount = countWords(input, "SAMX")
        val forwardCount = countWords(input, "XMAS")
        return backwardsCount + forwardCount
    }

    fun isXMAS(
        dirs: List<Cell>,
        input: List<String>,
    ): Boolean {
        var mCount = 0
        var sCount = 0
        for ((row, col) in dirs) {
            if (input[row][col] == 'M') mCount++
            if (input[row][col] == 'S') sCount++
        }

        return (mCount == 2 && sCount == 2 &&
                //topLeft vs bottomRight
                input[dirs[0].r][dirs[0].c] != input[dirs[2].r][dirs[2].c] &&
                //topRight vs bottomLeft
                input[dirs[1].r][dirs[1].c] != input[dirs[3].r][dirs[3].c])
    }

    fun part2(input: List<String>): Int {
        val rows = input.size
        val cols = input[0].length

        var count = 0
        //A's that are on the edges can't form X's
        for (r in 1 until rows - 1) {
            for (c in 1 until cols - 1) {
                if (input[r][c] == 'A') {
                    val dDirs = listOf(
                        Cell(r - 1, c - 1), //topLeft
                        Cell(r - 1, c + 1), //topRight
                        Cell(r + 1, c + 1), //bottomRight
                        Cell(r + 1, c - 1), //bottomLeft
                    )
                    if (isXMAS(dDirs, input)) count++
                }
            }
        }

        return count
    }

    val testInput = readInput("Day04", true, 1)
    val part1 = part1(testInput)
    check(part1 == 18)

    val testInput2 = readInput("Day04", true, 2)
    val part2 = part2(testInput2)
    check(part2 == 9)

    val input = readInput("Day04", false)
    part1(input).println()
    part2(input).println()
}

data class Cell(val r: Int, val c: Int)

enum class Direction {
    HORIZONTAL,
    VERTICAL,
    LEFT_RIGHT_DIAGONAL,
    RIGHT_LEFT_DIAGONAL
}