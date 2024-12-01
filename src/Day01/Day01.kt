import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val (leftList, rightList) = input
            .map { line ->
                line.split("   ").map { it.trim().toInt() }.zipWithNext().first()
            }
            .unzip()
            .let {
                it.first.sorted() to it.second.sorted()
            }

        var sum = 0
        for (i in leftList.indices) {
            val d = abs(leftList[i] - rightList[i])
            sum += d
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val leftList = mutableListOf<Int>()
        val rightCounts = mutableMapOf<Int, Int>()

        input.forEach { line ->
            val (left, right) = line.split("   ").map { it.trim().toInt() }

            leftList.add(left)
            rightCounts[right] = rightCounts.getOrDefault(right, 0) + 1
        }

        return leftList.sumOf { rightCounts.getOrDefault(it, 0) * it }
    }

    val testInput = readInput("Day01", true, 1)
    check(part1(testInput) == 11)

    val testInput2 = readInput("Day01", true, 2)
    check(part2(testInput2) == 31)

    val input = readInput("Day01", false)
    part1(input).println()
    part2(input).println()
}