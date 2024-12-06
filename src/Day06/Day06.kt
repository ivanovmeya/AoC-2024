fun main() {


    fun parseMap(input: List<String>): Pair<Pair<Int, Int>, MutableList<MutableList<Char>>> {

        var guard = 0 to 0

        for (row in input.indices) {
            val line = input[row]
            val index = line.indexOf('^')
            if (index != -1) {
                guard = row to index
                break
            }
        }


        val map = input.mapIndexed { row, line ->
            line.map { if (it == '#') it else '.' }.toMutableList()
        }.toMutableList()

        return guard to map

    }

    fun inBounds(point: Pair<Int, Int>, map: List<List<Char>>): Boolean {
        val (x, y) = point
        return x >= 0 && y >= 0 && x < map.size && y < map[0].size
    }

    fun turnRight(dir: Pair<Int, Int>): Pair<Int, Int> {
        return when (dir) {
            -1 to 0 -> 0 to 1 //from UP to RIGHT
            0 to 1 -> 1 to 0 //from RIGHT to DOWN
            1 to 0 -> 0 to -1 //from DOWN to LEFT
            0 to -1 -> ((-1) to 0) //from LEFT to UP
            else -> throw IllegalStateException("Wrong direction")
        }
    }

    fun performSteps(
        guard: Pair<Int, Int>,
        map: List<List<Char>>,
        updatePath: (step: Step) -> Boolean,
        onTurn: (step: Step) -> Unit
    ) {
        val step = Step(guard.first, guard.second, -1 to 0)
        while (inBounds(step.row to step.col, map)) {
            if (updatePath(step)) return
            //check if can make a step, otherwise turn until can make a new step
            var nextRow = step.row + step.dir.first
            var nextCol = step.col + step.dir.second
            while (inBounds(nextRow to nextCol, map) && map[nextRow][nextCol] == '#') {
                //turn 90 degree right
                val newDir = turnRight(step.dir)
                step.dir = newDir
                onTurn(step)
                nextRow = step.row + newDir.first
                nextCol = step.col + newDir.second
            }
            //perform step
            step.row += step.dir.first
            step.col += step.dir.second
        }
    }

    fun part1(input: List<String>): Int {
        val (guard, map) = parseMap(input)
        //perform steps
        val distinctPositions = hashSetOf<Pair<Int, Int>>()

        performSteps(
            guard = guard,
            map = map,
            updatePath = { step ->
                distinctPositions.add(step.row to step.col)
                false
            },
            onTurn = { }
        )

        return distinctPositions.size
    }

    fun detectLoop(step: Step, path: List<Step>) = path.contains(step)

    fun part2Bruteforce(input: List<String>): Int {

        val (guard, map) = parseMap(input)

        val result = mutableListOf<Pair<Int, Int>>()


        //try all possible positions - just brute force every position.
        for (i in map.indices) {
            for (j in map[0].indices) {
                val save = map[i][j]
                if (save == '#' || save == '^') continue
                //update map
                map[i][j] = '#'

                val path = mutableListOf<Step>()
                //determine we are in loop.
                var loopDetected = false
                performSteps(
                    guard = guard,
                    map = map,
                    updatePath = { step ->
                        //Check if we are in a loop, if so, break the cycle
                        loopDetected = detectLoop(step, path)
                        path.add(step.copy())
                        loopDetected
                    },
                    onTurn = {}
                )
                if (loopDetected) {
                    result.add(i to j)
                }

                map[i][j] = save
            }
        }
        return result.count()
    }

    fun part2(input: List<String>): Int {
        return part2Bruteforce(input)

    }

    val testInput = readInput("Day06", true, 1)
    val part1 = part1(testInput)
    println("part1 = $part1")
    check(part1 == 41)

    val testInput2 = readInput("Day06", true, 2)
    val part2 = part2(testInput2)
    println("part2 = $part2")
    check(part2 == 6)

    val input = readInput("Day06", false)
    part1(input).println()
    part2(input).println()
}

data class Step(var row: Int, var col: Int, var dir: Pair<Int, Int>)