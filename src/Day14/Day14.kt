fun main() {

    fun parse(input: List<String>): List<Robot> {
        return input.map { line ->
            val (pS, vS) = line.split(" ")
            val (x, y) = pS.substringAfter("=").trim().split(",").map { it.trim().toInt() }
            val (dx, dy) = vS.substringAfter("=").trim().split(",").map { it.trim().toInt() }

            Robot(PointInt(x, y), Velocity(dx, dy))
        }
    }


    fun moveRobots(robots: List<Robot>, wide: Int, tall: Int, seconds: Int): Array<IntArray> {

        val board = Array<IntArray>(tall) { IntArray(wide) { 0 } }

        for (r in robots) {

            var newX = (r.p.x + r.v.dx * seconds) % wide
            var newY = (r.p.y + r.v.dy * seconds) % tall

            newX = if (newX < 0) wide + newX else newX
            newY = if (newY < 0) tall + newY else newY

            board[newY][newX] += 1
        }
        return board
    }

    fun printBoard(board: Array<IntArray>) {
        for (i in board.indices) {
            for (j in board[0].indices) {

                val cell = board[i][j]
                if (cell == 0) print(" ") else print("*")

            }
            println()
        }
    }

    fun part1(input: List<String>, wide: Int, tall: Int): Int {

        val robots = parse(input)
        val seconds = 100

        val board = moveRobots(robots, wide, tall, seconds)

        val midW = wide / 2
        val midT = tall / 2
        val quadrants = IntArray(4) { 0 }
        for (y in board.indices) {
            for (x in board[0].indices) {
                if (x == midW || y == midT) continue
                val q = when {
                    x < midW && y < midT -> 0
                    x < midW && y > midT -> 2
                    x > midW && y < midT -> 1
                    x > midW && y > midT -> 3
                    else -> 0
                }

                quadrants[q] += board[y][x]
            }
        }

        var safetyFactor = 1
        quadrants.forEach {
            safetyFactor *= it
        }
        return safetyFactor
    }

    fun checkXMasTree(b: Array<IntArray>): Boolean {
        for (i in 5 until b.lastIndex - 15) {
            for (j in 5 until b[0].lastIndex - 5) {

                if (b[i][j] != 0) {
                    //check 3 levels of X Mas Tree
                    if (b[i + 1][j - 1] != 0 && b[i + 1][j] != 0 && b[i + 1][j + 1] != 0 &&
                        b[i + 1][j - 2] != 0 && b[i + 1][j] != 0 && b[i + 1][j + 2] != 0 &&
                        b[i + 2][j - 3] != 0 && b[i + 2][j - 2] != 0 && b[i + 2][j - 1] != 0 && b[i + 2][j] != 0 && b[i + 2][j + 1] != 0 && b[i + 2][j + 2] != 0 && b[i + 2][j + 3] != 0
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun part2(input: List<String>, wide: Int, tall: Int): Int {
        val robots = parse(input)

        for (s in 1 until 1352382) {
            val board = moveRobots(robots, wide, tall, s)
            if (checkXMasTree(board)) {
                println("********* XMASSSS $s second *********")
                printBoard(board)
                return s
            }
        }

        return 0
    }

    val testInput = readInput("Day14", true, 1)
    check(part1(testInput, 11, 7) == 12)

    val input = readInput("Day14", false)
    part1(input, 101, 103).println()
    part2(input, 101, 103).println()
}

data class Velocity(val dx: Int, val dy: Int)

data class Robot(val p: PointInt, val v: Velocity)

