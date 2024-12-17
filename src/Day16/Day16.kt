import Direction.Companion.clockwise
import Direction.Companion.counterclockwise
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.system.measureTimeMillis

fun main() {

    fun parse(input: List<String>): List<CharArray> {
        return input.map { it.toCharArray() }
    }

    fun backtrack(
        score: Long,
        row: Int,
        col: Int,
        d: Direction,
        scores: MutableList<Long>,
        maze: List<CharArray>,
        path: MutableList<Pair<Int, Int>>
    ) {
        if (maze[row][col] == '#') return

        if (path.contains(row to col)) return

        if (maze[row][col] == 'E') {
            scores.add(score)
            return
        }

        path.add(row to col)
        //search in 4 directions
        //up
        val upScore = when (d) {
            Direction.RIGHT -> 1001
            Direction.DOWN -> 2001
            Direction.LEFT -> 1001
            Direction.UP -> 1
        }
        backtrack(score + upScore, row - 1, col, Direction.UP, scores, maze, path)

        //right
        val rightScore = when (d) {
            Direction.RIGHT -> 1
            Direction.DOWN -> 1001
            Direction.LEFT -> 2001
            Direction.UP -> 1001
        }
        backtrack(score + rightScore, row, col + 1, Direction.RIGHT, scores, maze, path)

        //down
        val downScore = when (d) {
            Direction.RIGHT -> 1001
            Direction.DOWN -> 1
            Direction.LEFT -> 1001
            Direction.UP -> 2001
        }
        backtrack(score + downScore, row + 1, col, Direction.DOWN, scores, maze, path)

        //left
        val leftScore = when (d) {
            Direction.RIGHT -> 2001
            Direction.DOWN -> 1001
            Direction.LEFT -> 1
            Direction.UP -> 1001
        }
        backtrack(score + leftScore, row, col - 1, Direction.LEFT, scores, maze, path)

        path.remove(row to col)
    }


    fun solveWithBacktracking(maze: List<CharArray>): Long {
        val scores = mutableListOf<Long>()
        val path = mutableListOf<Pair<Int, Int>>()
        backtrack(0L, maze.lastIndex - 1, 1, Direction.RIGHT, scores, maze, path)

        val min = scores.min()

        scores.println()
        println(min)
        return min
    }


    fun getScore(d: Direction, movingDirection: Direction): Int {
        return when (movingDirection) {
            Direction.RIGHT -> when (d) {
                Direction.RIGHT -> 1
                Direction.DOWN -> 1001
                Direction.LEFT -> 2001
                Direction.UP -> 1001
            }

            Direction.DOWN -> when (d) {
                Direction.RIGHT -> 1001
                Direction.DOWN -> 1
                Direction.LEFT -> 1001
                Direction.UP -> 2001
            }

            Direction.LEFT -> when (d) {
                Direction.RIGHT -> 2001
                Direction.DOWN -> 1001
                Direction.LEFT -> 1
                Direction.UP -> 1001
            }

            Direction.UP -> when (d) {
                Direction.RIGHT -> 1001
                Direction.DOWN -> 2001
                Direction.LEFT -> 1001
                Direction.UP -> 1
            }
        }
    }


    fun dijkstraAllPaths(maze: List<CharArray>): Int {

        val q = PriorityQueue<NodeWithScore>(compareBy { it.score })
        val sN = Node(maze.size - 2, 1, Direction.RIGHT)
        val start = NodeWithScore(0, sN.r, sN.c, Direction.RIGHT)
        q.add(start)

        //It could be a set
        val scores = mutableMapOf<Node, Int>()
        scores[sN] = 0


        //best costs - not understand why we need it
        var bestScore = Int.MAX_VALUE
        val endStates = mutableSetOf<Node>()

        val backtrack = mutableMapOf<Node, MutableSet<Node>>()

        while (q.isNotEmpty()) {
            val nodeS = q.poll()

            //we already found better score for this node, no need to process it
            val node = Node(nodeS.r, nodeS.c, nodeS.d)
            if (nodeS.score > scores.getOrDefault(node, Int.MAX_VALUE)) continue

            if (maze[nodeS.r][nodeS.c] == 'E') {
                //If we are processing node, that is more then our best score and the end 'E' -> it means we already processed all best paths and need to finish
                if (nodeS.score > bestScore) break
                bestScore = nodeS.score
                endStates.add(node)
            }

            val nextNodes = listOf(
                NodeWithScore(nodeS.score + 1, nodeS.r + nodeS.d.dr, nodeS.c + nodeS.d.dc, nodeS.d), //move forward
                NodeWithScore(nodeS.score + 1000, nodeS.r, nodeS.c, nodeS.d.clockwise()), //clockwise
                NodeWithScore(nodeS.score + 1000, nodeS.r, nodeS.c, nodeS.d.counterclockwise()) //counterclockwise
            )

            for (nextNodeS in nextNodes) {
                val (newCost, nr, nc, nd) = nextNodeS
                if (maze[nr][nc] == '#') continue

                //scores always contains lowest known scores for the node
                val nextNode = Node(nr, nc, nd)
                val lowest = scores.getOrDefault(nextNode, Int.MAX_VALUE)
                if (newCost > lowest) continue

                if (newCost < lowest) {
                    //clean backtrack,
                    backtrack[nextNode] = mutableSetOf()
                    //set new lowest cost
                    scores[nextNode] = newCost
                }

                //if (newCost == lowest) -> we still need to add to backtrack, so it is outside the above if
                backtrack[nextNode] = backtrack.getOrDefault(nextNode, mutableSetOf()).apply {
                    add(node)
                }

                q.add(nextNodeS)
            }
        }

        println(endStates)
        println()
        for(node in backtrack.keys) {
            println("$node: ${backtrack[node]!!.joinToString(", ")}")
        }

        val states = ArrayDeque<Node>()
        val seen = hashSetOf<Node>()

        seen.addAll(endStates)
        states.addAll(endStates)

        while(states.isNotEmpty()) {
            val key = states.removeFirst()

            for (prev in backtrack.getOrDefault(key, hashSetOf())) {
                if (seen.contains(prev)) continue
                seen.add(prev)
                states.addLast(prev)
            }
        }

        val set = seen.map { it.r to it.c }.toSet()

        return set.size
    }

    //Without comparing if total cost is less, than already saved cost for this node and cost. We just process them all, and it's working. fuuuuck
    fun dijkstraNoCostComparison(maze: List<CharArray>): Int {

        val q = PriorityQueue<NodeWithScore>(compareBy { it.score })
        val start = NodeWithScore(0, maze.size - 2, 1, Direction.RIGHT)
        q.add(start)

        val seen = hashSetOf<Node>()

        while (q.isNotEmpty()) {
            val node = q.poll()
            seen.add(Node(node.r, node.c, node.d))
//            println("processing = [${node.r},${node.c}]")
            if (maze[node.r][node.c] == 'E') return node.score

            val nextNodes = listOf(
                NodeWithScore(node.score + 1, node.r + node.d.dr, node.c + node.d.dc, node.d), //move forward
                NodeWithScore(node.score + 1000, node.r, node.c, node.d.clockwise()), //clockwise
                NodeWithScore(node.score + 1000, node.r, node.c, node.d.counterclockwise()) //counterclockwise
            )

            for (n in nextNodes) {
                val (newCost, nr, nc, nd) = n
                if (maze[nr][nc] == '#') continue
                if (seen.contains(Node(nr, nc, nd))) continue

                q.add(n)
            }
        }

        return -1
    }

    //Without comparing if total cost is less, than already saved cost for this node and cost. We just process them all, and it's working. fuuuuck
    fun dijkstraCostComparison(maze: List<CharArray>): Int {

        val q = PriorityQueue<NodeWithScore>(compareBy { it.score })
        val sN = Node(maze.size - 2, 1, Direction.RIGHT)
        val start = NodeWithScore(0, sN.r, sN.c, Direction.RIGHT)
        q.add(start)

        val costs = mutableMapOf<Node, Int>()
        costs[sN] = 0

        val seen = hashSetOf<Node>()

        while (q.isNotEmpty()) {
            val node = q.poll()
            seen.add(Node(node.r, node.c, node.d))
            if (maze[node.r][node.c] == 'E') return node.score

            val nextNodes = listOf(
                NodeWithScore(node.score + 1, node.r + node.d.dr, node.c + node.d.dc, node.d), //move forward
                NodeWithScore(node.score + 1000, node.r, node.c, node.d.clockwise()), //clockwise
                NodeWithScore(node.score + 1000, node.r, node.c, node.d.counterclockwise()) //counterclockwise
            )

            for (n in nextNodes) {
                val (newCost, nr, nc, nd) = n
                if (maze[nr][nc] == '#') continue
                if (seen.contains(Node(nr, nc, nd))) continue

                if (newCost < (costs[Node(nr, nc, nd)] ?: Int.MAX_VALUE)) {
                    q.add(n)
                    costs[Node(nr, nc, nd)] = newCost
                }
            }
        }

        return -1
    }

    fun part1(input: List<String>): Int {
        val maze = parse(input)

        var cost1: Int

        val t1 = measureTimeMillis {
            cost1 = dijkstraNoCostComparison(maze)
        }

        var cost2: Int

        val t2 = measureTimeMillis {
            cost2 = dijkstraCostComparison(maze)
        }

        println("Dijkstra time: noComparison = $t1, withComparison = $t2. Answers: $cost1; $cost2")

        return cost2
    }


    fun part2(input: List<String>): Int {
        val maze = parse(input)
        return dijkstraAllPaths(maze)
    }

    val testInput = readInput("Day16", true, 1)
    val testInput2 = readInput("Day16", true, 2)

    val part1Test1 = part1(testInput)
    println("part1Test1=$part1Test1")
    check(part1Test1 == 7036)
    val part1Test2 = part1(testInput2)
    println("part1Test2=$part1Test2")
    check(part1Test2 == 11048)

    val part2Test1 = part2(testInput)
    println("part2Test1=$part2Test1")
    check(part2Test1 == 45)
    val part2Test2 = part2(testInput2)
    println("part2Test2=$part2Test2")
    check(part2Test2 == 64)

    val input = readInput("Day16", false)
    part1(input).println()
    part2(input).println()
}


data class NodeWithScore(val score: Int, val r: Int, val c: Int, val d: Direction)
data class Node(val r: Int, val c: Int, val d: Direction)