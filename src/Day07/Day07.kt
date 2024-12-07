fun main() {
    fun parse(input: List<String>): List<Pair<Long, List<Long>>> {
        return input.map { line ->
            val (target, numberLine) = line.split(':')
            val numbers = numberLine.trim().split(" ").map { it.trim().toLong() }

            target.toLong() to numbers
        }
    }

    fun operate(op: Char, num1: Long, num2: Long): Long {
        return when (op) {
            '+' -> num1 + num2
            '*' -> num1 * num2
            '.' -> "$num1$num2".toLong()
            else -> throw IllegalStateException("Unsupported operation")
        }
    }

    fun backtracking(
        numbers: List<Long>,
        i: Int,
        operations: List<Char>,
        target: Long,
        total: Long,
    ): Boolean {
        if (total == target) {
            return true
        }
        if (i == numbers.size) {
            return false
        }

        for (op in operations) {
            if (backtracking(numbers, i + 1, operations, target, operate(op, total, numbers[i])))
                return true
        }

        return false
    }

    fun calibrationResult(input: List<String>, operations: List<Char>): Long {
        val lines = parse(input)
        return lines.sumOf { line ->
            val (target, numbers) = line
            if (backtracking(numbers, 1, operations, target, numbers[0])) target else 0
        }
    }

    fun part1(input: List<String>): Long {
        val operations = listOf('+', '*')
        return calibrationResult(input, operations)
    }

    fun part2(input: List<String>): Long {
        val operations = listOf('+', '*', '.')
        return calibrationResult(input, operations)
    }

    val testInput = readInput("Day07", true, 1)
    check(part1(testInput) == 3749L)

    val testInput2 = readInput("Day07", true, 2)
    check(part2(testInput2) == 11387L)

    val input = readInput("Day07", false)
    part1(input).println()
    part2(input).println()
}