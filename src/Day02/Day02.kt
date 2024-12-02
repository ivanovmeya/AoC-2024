fun main() {
    fun isReportSafe(levels: List<Int>, isAscending: Boolean): Boolean {
        var safe = true
        for (i in 1..levels.lastIndex) {
            val diff = levels[i] - levels[i - 1]

            safe = if (isAscending) {
                diff in 1..3
            } else {
                diff >= -3 && diff < 0
            }
            if (!safe) break
        }
        return safe
    }

    fun isReportAscending(levels: List<Int>): Boolean {
        var ascCount = 0
        levels.forEachIndexed { index, level ->
            if (index != 0) {
                if (level > levels[index - 1]) ascCount++
            }
        }

        val isAscending = ascCount > levels.count() / 2
        return isAscending
    }

    fun part1(input: List<String>): Int {

        return input.count { report ->
            val levels = report.split(" ").map { it.trim().toInt() }
            val isAscending = (levels[1] - levels[0]) > 0

            isReportSafe(levels, isAscending)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { report ->
            val levels = report.split(" ").map { it.trim().toInt() }

            val isAscending = isReportAscending(levels)
            //test new report by trying to remove each of the levels, start without removals (badIndex = -1)
            for (badIndex in -1..levels.lastIndex) {
                val newLevels = levels.toMutableList().apply {
                    if (badIndex != -1) {
                        removeAt(badIndex)
                    }
                }

                if (isReportSafe(newLevels, isAscending)) return@count true
            }
            false
        }
    }

    val testInput = readInput("Day02", true, 1)
    check(part1(testInput) == 2)

    val testInput2 = readInput("Day02", true, 2)
    check(part2(testInput2) == 4)

    val input = readInput("Day02", false)
    part1(input).println()
    part2(input).println()
}