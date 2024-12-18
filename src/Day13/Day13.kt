fun main() {

    fun parse(input: List<String>): List<Game> {
        return input.filterNot { it.isEmpty() }.windowed(3, 3) { lines ->

            val (a, b, price) = lines.map { line ->
                val (xStr, yStr) = line.split(",")
                val dx = xStr.substring(xStr.indexOfFirst { it.isDigit() }).trim().toInt()
                val dy = yStr.substring(yStr.indexOfFirst { it.isDigit() }).trim().toInt()
                PointLong(dx.toLong(), dy.toLong())
            }
            Game(
                a = a,
                b = b,
                prize = price
            )
        }
    }

    fun calcTokens(games: List<Game>): Long {
        return games.sumOf { g ->

            val bTop = g.a.x * g.prize.y - g.a.y * g.prize.x
            val bBottom = (g.a.x * g.b.y - g.a.y * g.b.x)

            val timeBisWhole = bTop % bBottom == 0L

            if (timeBisWhole) {
                val timesB = bTop / bBottom
                val aTop = (g.prize.x - g.b.x * timesB)
                val aBottom = g.a.x
                val timesA = aTop / aBottom
                if (aTop % aBottom == 0L) {
                    timesA * 3 + timesB
                } else {
                    0
                }
            } else {
                0
            }
        }
    }

    fun part1(input: List<String>): Int {
        val games = parse(input)
        return calcTokens(games).toInt()
    }

    fun part2(input: List<String>): Long {
        val games = parse(input)
        val bigGames = games.map { game ->
            Game(
                game.a,
                game.b,
                PointLong(game.prize.x + 10000000000000, game.prize.y + 10000000000000)
            )
        }
        return calcTokens(bigGames)
    }

    val testInput = readInput("Day13", true, 1)
    check(part1(testInput) == 480)

    val testInput2 = readInput("Day13", true, 2)
    check(part2(testInput2) == 875318608908L)

    val input = readInput("Day13", false)
    part1(input).println()
    part2(input).println()
}

data class Game(val a: PointLong, val b: PointLong, val prize: PointLong) {
    override fun toString(): String {
        return "A:(${a.x},${a.y}); B:(${b.x},${b.y}); Prize = (${prize.x},${prize.y})"
    }
}