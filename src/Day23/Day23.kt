fun main() {
    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day23", true, 1)
    check(part1(testInput) == 0)

    val testInput2 = readInput("Day23", true, 2)
    check(part2(testInput2) == 0)

    val input = readInput("Day23", false)
    part1(input).println()
    part2(input).println()
}