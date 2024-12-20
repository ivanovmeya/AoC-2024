fun main() {
    fun parse(input: List<String>): Array<CharArray> {
        val smth = input.map {
            it.toCharArray()
        }.toTypedArray()
        return smth
    }

    fun findStartAndFinish(map: Array<CharArray>): Pair<Cell, Cell> {
        var s: Cell? = null
        var e: Cell? = null
        for (r in map.indices) {
            for (c in map[0].indices) {
                if (map[r][c] == 'S') {
                    s = Cell(r, c)
                } else if (map[r][c] == 'E') {
                    e = Cell(r, c)
                }
            }
        }

        return s!! to e!!
    }

    fun bfs(s: Cell, e: Cell, map: Array<CharArray>): Array<IntArray> {

        val scoreMap = Array(map.size) { IntArray(map[0].size) { -1 } }

        val q = ArrayDeque<CellWithStep>()
        q.add(CellWithStep(s, 0, s))

        val visited = hashSetOf<Cell>()

        while (q.isNotEmpty()) {
            val (c, step, _) = q.removeFirst()
            if (visited.contains(c)) continue
            scoreMap[c.r][c.c] = step
            visited.add(c)

            if (c == e) return scoreMap

            for (d in Direction.all()) {
                val nr = c.r + d.dr
                val nc = c.c + d.dc
                if (map[nr][nc] != '#') {
                    q.addLast(CellWithStep(Cell(nr, nc), step + 1, c))
                }
            }
        }

        return scoreMap
    }

    fun cheatCountsMoreThreshold(
        map: Array<CharArray>,
        scoreMap: Array<IntArray>,
        threshold: Int,
        maxCheatSteps: Int
    ): Int {
        var count = 0
        for (r in map.indices) {
            for (c in map[0].indices) {
                if (map[r][c] == '#') continue

                for (radius in 2..maxCheatSteps) {
                    for (dr in 0..radius) {
                        val dc = radius - dr
                        for ((nr, nc) in setOf(
                            r + dr to c + dc,
                            r + dr to c - dc,
                            r - dr to c + dc,
                            r - dr to c - dc
                        )) {
                            if (nr < 0 || nc < 0 || nr >= map.size || nc >= map[0].size) continue
                            if (map[nr][nc] == '#') continue
                            if ((scoreMap[nr][nc] - scoreMap[r][c]) >= threshold + radius) count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun part1(input: List<String>, threshold: Int): Int {
        val map = parse(input)
        val (s, e) = findStartAndFinish(map)
        val scoreMap = bfs(s, e, map)
        return cheatCountsMoreThreshold(map, scoreMap, threshold, 2)

    }

    fun part2(input: List<String>, threshold: Int): Int {
        val map = parse(input)
        val (s, e) = findStartAndFinish(map)
        val scoreMap = bfs(s, e, map)
        return cheatCountsMoreThreshold(map, scoreMap, threshold, 20)

    }

    val testInput = readInput("Day20", true, 1)
    check(part1(testInput, 12) == 8)
    check(part2(testInput, 72) == 29)


    val input = readInput("Day20", false)
    part1(input, 100).println()
    part2(input, 100).println()
}

data class CellWithStep(val c: Cell, val step: Int, val prev: Cell = Cell(0, 0))