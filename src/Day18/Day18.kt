fun main() {

    fun parse(input: List<String>, size: Int, bytes: Int): Array<CharArray> {

        val r = Array<CharArray>(size) { CharArray(size) { '.' } }

        for (byte in input.take(bytes).map { it.split(",").map { it.toInt() } }.map { Point(it[0], it[1]) }) {
            r[byte.y][byte.x] = '#'
        }

        return r

    }

    fun parse2(input: List<String>, size: Int, bytes: Int): Pair<Array<CharArray>, List<Point>> {

        val r = Array<CharArray>(size) { CharArray(size) { '.' } }

        val allBytes = input.map { it.split(",").map { it.toInt() } }.map { Point(it[0], it[1]) }

        for (byte in allBytes.take(bytes)) {
            r[byte.y][byte.x] = '#'
        }

        return r to allBytes.subList(bytes, allBytes.size)

    }

    fun bfs(x: Int, y: Int, map: Array<CharArray>): Int {

        val q = ArrayDeque<PointWithSteps>()
        q.add(PointWithSteps(x, y, 0))
        val seen = hashSetOf<Point>()

        while (q.isNotEmpty()) {

            val p = q.removeFirst()
            if (seen.contains(Point(p.x, p.y))) continue

            if (p.x == map[0].lastIndex && p.y == map.lastIndex) {
                return p.steps
            }

            seen.add(Point(p.x, p.y))

            //add all neighbors
            for (d in Direction.all()) {
                val nx = p.x + d.dc
                val ny = p.y + d.dr

                if (nx < 0 || ny < 0 || nx >= map[0].size || ny >= map.size) continue
                if (map[ny][nx] == '#') continue

                q.addLast(PointWithSteps(nx, ny, p.steps + 1))
            }

        }

        return 0
    }

    fun part1(input: List<String>, size: Int, bytes: Int = 12): Int {

        val map = parse(input, size, bytes)

        for (i in map.indices) {
            for (j in map[0].indices) {
                print("${map[i][j]}")
            }
            println()
        }

        //the graph is not weighted. so simple dfs would be good enough
        return bfs(0, 0, map)
    }

    fun part2(input: List<String>, size: Int, initialBytes: Int): Point {

        val (map, bytes) = parse2(input, size, initialBytes)


        for (byte in bytes) {
            map[byte.y][byte.x] = '#'
            if (bfs(0, 0, map) == 0) {
                return byte
            }
        }
        return Point(0,0)
    }

    val testInput = readInput("Day18", true, 1)
    check(part2(testInput, 7, 12) == Point(6,1))


    val input = readInput("Day18", false)
    part1(input, 71, 1024).println()
    part2(input, 71, 1024).println()
}


data class PointWithSteps(val x: Int, val y: Int, val steps: Int)