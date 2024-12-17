fun main() {

    fun parse(input: List<String>): Triple<PointInt, MutableList<MutableList<Char>>, List<Char>> {

        var robot = PointInt(0, 0)
        val map = mutableListOf<MutableList<Char>>()
        val commands = mutableListOf<Char>()

        val emptyIndex = input.indexOfFirst { it.isEmpty() }

        input.forEachIndexed { i, line ->
            if (i < emptyIndex) {
                map.add(mutableListOf<Char>())
            }
            line.forEachIndexed { j, ch ->
                if (i < emptyIndex) {
                    //parse map
                    if (ch == '@') {
                        robot = PointInt(i, j)
                        map[i].add('.')
                    } else {
                        map[i].add(ch)
                    }
                }
                if (i > emptyIndex) {
                    //parse commands
                    commands.add(ch)
                }
            }
        }

        return Triple(robot, map, commands)
    }

    fun widenMap(input: List<String>): List<String> {
        val emptyIndex = input.indexOfFirst { it.isEmpty() }

        val map = input.subList(0, emptyIndex)

        val list = mutableListOf<String>()

        map.forEach { s ->
            val sb = StringBuilder()
            for (ch in s) {
                val toAppend = when (ch) {
                    '#' -> "##"
                    'O' -> "[]"
                    '.' -> ".."
                    '@' -> "@."
                    else -> throw IllegalArgumentException("Unknown ch = $ch")
                }
                sb.append(toAppend)
            }
            list.add(sb.toString())
        }
        list.addAll(input.subList(emptyIndex, input.size))

        return list
    }

    fun move(r: PointInt, d: Direction, map: MutableList<MutableList<Char>>) {
        //try to move robot, check surroundings,
        val newX = r.x + d.dr
        val newY = r.y + d.dc

        val ch = map[newX][newY]

        when (ch) {
            '.' -> {
                r.x = newX
                r.y = newY
            }

            '#' -> {
                //don't do anything
            }

            'O' -> {
                //Fun begins here
                //now we need to check surroundings of this 0 in same direction, until we found empty space, or #
                var x = newX + d.dr
                var y = newY + d.dc

                while (map[x][y] != '#' && map[x][y] != '.') {
                    x += d.dr
                    y += d.dc
                }

                //if we found
                if (map[x][y] == '#') {
                    //do nothing
                } else {
                    //move everything one step in dir from newX, newY to x,y
                    //the cell of newX, newY -> swap it to x,y - and we are done
                    r.x = newX
                    r.y = newY
                    map[x][y] = 'O'
                    map[newX][newY] = '.'
                }
            }
        }
    }

    fun isMovable(
        box: Box, d: Direction,
        map: MutableList<MutableList<Char>>,
        targets: HashSet<Box>
    ): Boolean {

        //check below
        val p1Xn = box.p1.x + d.dr
        val p1Yn = box.p1.y + d.dc

        val p2Xn = box.p2.x + d.dr
        val p2Yn = box.p2.y + d.dc

        if (map[p1Xn][p1Yn] == '#' || map[p2Xn][p2Yn] == '#') return false

        targets.add(box) //it could be that we will end up moving current box. but we need to check further

        if (map[p1Xn][p1Yn] == '.' && map[p2Xn][p2Yn] == '.') return true

        val isLeft = if (map[p1Xn][p1Yn] != '.') {
            val p1ShiftY = if (map[p1Xn][p1Yn] == '[') +1 else -1
            val y = p1Yn + p1ShiftY
            val newBox1 = if (map[p1Xn][p1Yn] == '[') {
                Box(map[p1Xn][p1Yn], PointInt(p1Xn, p1Yn), map[p1Xn][y], PointInt(p1Xn, y))
            } else {
                Box(map[p1Xn][y], PointInt(p1Xn, y), map[p1Xn][p1Yn], PointInt(p1Xn, p1Yn))
            }

            isMovable(newBox1, d, map, targets)
        } else true

        val isRight = if (map[p2Xn][p2Yn] != '.') {
            val p2ShiftY = if (map[p2Xn][p2Yn] == '[') +1 else -1
            val y = p2Yn + p2ShiftY
            val newBox2 = if (map[p2Xn][p2Yn] == '[') {
                Box(map[p2Xn][p2Yn], PointInt(p2Xn, p2Yn), map[p2Xn][y], PointInt(p2Xn, y))
            } else {
                Box(map[p2Xn][y], PointInt(p2Xn, y), map[p2Xn][p2Yn], PointInt(p2Xn, p2Yn))
            }
            isMovable(newBox2, d, map, targets)
        } else true

        return isLeft && isRight
    }

    fun move2(r: PointInt, d: Direction, map: MutableList<MutableList<Char>>) {
        //try to move robot, check surroundings,
        val newX = r.x + d.dr
        val newY = r.y + d.dc

        when (val ch = map[newX][newY]) {
            '.' -> {
                r.x = newX
                r.y = newY
            }

            '#' -> {
                //don't do anything
            }

            '[', ']' -> {
                when (d) {
                    Direction.UP, Direction.DOWN -> {

                        val targets = hashSetOf<Box>()

                        val shiftY = if (ch == '[') +1 else -1
                        val y = newY + shiftY

                        val box = if (ch == '[') {
                            Box(ch, PointInt(newX, newY), map[newX][y], PointInt(newX, y))
                        } else{
                            Box(map[newX][y], PointInt(newX, y), ch, PointInt(newX, newY))
                        }

                        if (isMovable(box, d, map, targets)) {

                            r.x = newX
                            r.y = newY

                            val sortedTargets = targets.sortedBy { it.p1.x }
                            val pTargets = if (d == Direction.UP) sortedTargets else sortedTargets.reversed()
                            //moving
                            for (t in pTargets) {
                                val p1 = PointInt(t.p1.x + d.dr, t.p1.y + d.dc)
                                val p2 = PointInt(t.p2.x + d.dr, t.p2.y + d.dc)

                                //move
                                map[p1.x][p1.y] = t.ch1
                                map[p2.x][p2.y] = t.ch2

                                //clean
                                map[t.p1.x][t.p1.y] = '.'
                                map[t.p2.x][t.p2.y] = '.'

                            }
                        }
                    }

                    Direction.LEFT, Direction.RIGHT -> {
                        var x = newX + 2 * d.dr
                        var y = newY + 2 * d.dc

                        while (map[x][y] != '#' && map[x][y] != '.') {
                            x += d.dr
                            y += d.dc
                        }

                        //if we found
                        if (map[x][y] == '#') {
                            //do nothing
                        } else {
                            r.x = newX
                            r.y = newY

                            //now it's not that easy anymore, as we have back and forth parenthesis, and we really need to swap them

                            //we start at newX,newY and finish at x,y in direction d

                            if (d.dc > 0) {
                                //going right
                                for (i in y downTo newY + d.dc) {
                                    map[x][i] = map[x][i - d.dc]
                                }
                            } else {
                                for (i in y..newY + d.dc) {
                                    map[x][i] = map[x][i - d.dc]
                                }
                            }

                            map[newX][newY] = '.'
                        }
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {

        val (robot, map, commands) = parse(input)

        commands.forEach { c ->
            val dir = when (c) {
                '^' -> Direction.UP
                '>' -> Direction.RIGHT
                'v' -> Direction.DOWN
                '<' -> Direction.LEFT
                else -> throw IllegalArgumentException("unknown command $c")
            }
            move(robot, dir, map)
        }

        var gpsSum = 0

        for (i in map.indices) {
            for (j in map[0].indices) {
                if (map[i][j] == 'O') {
                    gpsSum += i * 100 + j
                }
            }
        }

        println("gpsSum=$gpsSum")

        return gpsSum
    }


    fun part2(input: List<String>): Int {

        val widenInput = widenMap(input)

        println("Widen Input")
        widenInput.printList()

        val (robot, map, commands) = parse(widenInput)

        commands.forEach { c ->
            val dir = when (c) {
                '^' -> Direction.UP
                '>' -> Direction.RIGHT
                'v' -> Direction.DOWN
                '<' -> Direction.LEFT
                else -> throw IllegalArgumentException("unknown command $c")
            }
            move2(robot, dir, map)
        }

        var gpsSum = 0

        for (i in map.indices) {
            for (j in map[0].indices) {
                if (map[i][j] == '[') {
                    gpsSum += i * 100 + j
                }
            }
        }
        return gpsSum
    }

    val testInput = readInput("Day15", true, 1)
    val testInput2 = readInput("Day15", true, 2)
    val testInput3 = readInput("Day15", true, 3)

    check(part1(testInput) == 2028)
    check(part1(testInput2) == 10092)


    check(part2(testInput3) == 618)
    check(part2(testInput2) == 9021)

    val input = readInput("Day15", false)
    part1(input).println()
    part2(input).println()
}


data class Box(val ch1: Char, val p1: PointInt, val ch2: Char, val p2: PointInt)