fun main() {

    fun parse(input: List<String>): List<Long> {
        return input.first().split(" ").map { it.toLong() }
    }

    fun blink(stones: List<Long>): MutableList<Long> {
        val result = mutableListOf<Long>()
        stones.forEach {
            val stone = it.toString()
            when {
                stone == "0" -> result.add(1L)
                (stone.length % 2) == 0 -> {
                    //replace with 2 stones. and trim leading zeroes
                    val left = stone.substring(0, stone.length / 2)
                    val right = stone.substring(stone.length / 2)

                    result.add(left.toLong())
                    result.add(right.toLong())
                }

                else -> result.add(stone.toLong() * 2024)
            }
        }
        return result
    }

    fun dfs(stone: Long, stepsLeft: Int, memory: HashMap<Pair<Long, Int>, Long>): Long {
        if (stepsLeft == 0) {
            return 1
        }

        if (memory[stone to stepsLeft] != null) {
            return memory[stone to stepsLeft]!!
        }

        val stones = blink(listOf(stone))

        var count = 0L
        for (st in stones) {
            count += dfs(st, stepsLeft - 1, memory)
        }

        memory[stone to stepsLeft] = count

        return memory[stone to stepsLeft]!!

    }

    fun simulate(input: List<String>, targetSteps: Long): Long {
        val stones = parse(input)
        val memory = hashMapOf<Pair<Long, Int>, Long>()
        return stones.sumOf { stone ->
            dfs(stone, targetSteps.toInt(), memory)
        }
    }

    fun part1(input: List<String>): Long {
        var stones = parse(input)

        repeat(25) {
            stones = blink(stones)
        }

        return simulate(input, 25L)
    }


    fun part2(input: List<String>): Long {
        return simulate(input, 75L)
    }

    val testInput = readInput("Day11", true, 1)
    check(part1(testInput) == 55312L)

    val input = readInput("Day11", false)
    part1(input).println()
    part2(input).println()
}