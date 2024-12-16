import kotlin.math.abs

fun main() {
    fun parse(input: List<String>): List<List<Char>> {
        return input.map { it.toCharArray().toList() }
    }

    fun dfs(
        regionPlant: Char,
        cell: Cell,
        map: List<List<Char>>,
        visited: Array<BooleanArray>,
        region: MutableList<Cell>
    ) {

        if (!cell.inbounds(map) || map[cell.r][cell.c] != regionPlant || visited[cell.r][cell.c]) {
            return
        }

        visited[cell.r][cell.c] = true

        region.add(cell)

        dfs(regionPlant, cell.moveLeft(), map, visited, region)
        dfs(regionPlant, cell.moveUp(), map, visited, region)
        dfs(regionPlant, cell.moveRight(), map, visited, region)
        dfs(regionPlant, cell.moveDown(), map, visited, region)
    }

    fun perimeter(region: List<Cell>, map: List<List<Char>>): Int {
        var p = 0
        val firstCell = region.first()
        val plant = map[firstCell.r][firstCell.c]

        for (cell in region) {
            //check 4 surroundings to get the result
            val left = cell.moveLeft()
            val top = cell.moveUp()
            val right = cell.moveRight()
            val down = cell.moveDown()

            val sides = listOf(left, top, right, down)

            for (s in sides) {
                if (!s.inbounds(map) || map[s.r][s.c] != plant) p++
            }
        }
        return p
    }

    fun findRegions(map: List<List<Char>>): MutableList<MutableList<Cell>> {
        val regions = mutableListOf<MutableList<Cell>>()

        val visited = Array(map.size) { BooleanArray(map[0].size) { false } }

        for (r in map.indices) {
            for (c in map[0].indices) {
                if (!visited[r][c]) {
                    val region = mutableListOf<Cell>()
                    dfs(map[r][c], Cell(r, c), map, visited, region)
                    regions.add(region)
                }
            }
        }
        return regions
    }

    fun part1(input: List<String>): Int {
        val map = parse(input)
        val regions = findRegions(map)

        return regions.sumOf { region ->
            val area = region.size
            val p = perimeter(region, map)

            //how to get perimeter?
            area * p
        }
    }

    fun part2(input: List<String>): Int {
        val map = parse(input)
        val regions = findRegions(map)

        val totalPrice = regions.sumOf { region ->
            val area = region.size
            val edges = edgesInRegion(region, map)
            val sides2 = sidesFromEdges(edges)
            area * sides2
        }

        return totalPrice
    }

    val testInput = readInput("Day12", true, 1)
    val test0 = """AAAA
BBCD
BBCC
EEEC""".split("\n")

    val test1 = """OOOOO
OXOXO
OOOOO
OXOXO
OOOOO""".split("\n")

    check(part1(test0) == 140)
    check(part1(test1) == 772)
    check(part1(testInput) == 1930)


    val testE = """EEEEE
EXXXX
EEEEE
EXXXX
EEEEE""".split("\n")

    val testInput2 = readInput("Day12", true, 2)
    check(part2(test0) == 80)
    check(part2(test1) == 436)
    check(part2(testE) == 236)
    check(part2(testInput2) == 1206)

    val input = readInput("Day12", false)
    part1(input).println()
    part2(input).println()
}


fun sidesFromEdges(edges: HashSet<Edge>): Int {

    val visited = hashSetOf<Edge>()

    var sides = 0

    for (edge in edges) {
        //each edge could form a side. We take the edge and found all adjacent ones to it
        if (visited.contains(edge)) continue
        visited.add(edge)
        sides++
        var e = edge

        //search all adjacent edges
        //looks at 4 sides for this edge -- continue until we found all adjacent ones
        for (dir in Direction.all()) {

            var nextEdge = Edge(e.row + dir.dx, e.col + dir.dy, e.dir)

            //Check if this edge is in the edges itself
            while (edges.contains(nextEdge) && e.isAdjacent(nextEdge)) {
                visited.add(nextEdge)
                e = nextEdge
                nextEdge = Edge(nextEdge.row + dir.dx, nextEdge.col + dir.dy, nextEdge.dir)
            }
        }
    }

    return sides
}

//Does not consider if edge present in edges
fun Edge.isAdjacent(other: Edge): Boolean {
    return when {
        this.dir != other.dir || this.row != other.row && this.col != other.col -> false
        //probably this is not needed, as we iterate only to the possible adjacent edges (not random edges)
        this.row == other.row -> abs(this.col - other.col) <= 1
        this.col == other.col -> abs(this.row - other.row) <= 1
        else -> false
    }
}

fun edgesInRegion(region: List<Cell>, map: List<List<Char>>): HashSet<Edge> {
    val edges = hashSetOf<Edge>()
    for (plantCell in region) {
        val plant = map[plantCell.r][plantCell.c]
        //check surroundings
        for (dir in Direction.all()) {
            //if there are an edge between current plantCell and next one in that direction
            val nextPlantCell = Cell(plantCell.r + dir.dx, plantCell.c + dir.dy)
            val nextPlant = map.tryGetCell(nextPlantCell.r, nextPlantCell.c)

            //different plant or outbounds and null
            if (nextPlant != plant) {
                //we will add edge
                edges.add(
                    Edge(
                        row = (nextPlantCell.r + plantCell.r) / 2f,
                        col = (nextPlantCell.c + plantCell.c) / 2f,
                        dir = dir == Direction.RIGHT || dir == Direction.DOWN
                    )
                )
            }
        }
    }
    return edges
}

fun List<List<Char>>.tryGetCell(r: Int, c: Int): Char? = this.getOrNull(r)?.getOrNull(c)

data class Edge(val row: Float, val col: Float, val dir: Boolean) //forward or backwards
