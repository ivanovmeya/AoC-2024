fun main() {

    fun parse(input: List<String>): Pair<List<String>, List<String>> {
        val towels = input[0].split(",").map { it.trim() }
        val designs = input.subList(2, input.size).map { it.trim() }

        return towels to designs
    }

    fun partition2(design: String, towels: List<String>, memo: HashMap<String, Long>): Long {
        if (memo.containsKey(design)) {
            return memo[design]!!
        }

        var result = 0L

        if (design.isEmpty()) {
            result = 1L
        }

        for (t in towels) {
            if (design.startsWith(t)) {
                result += partition2(design.removePrefix(t), towels, memo)
            }
        }

        memo[design] = result
        return result
    }

    fun partition(design: String, towels: List<String>, memo: HashMap<String, Boolean>): Boolean {
        if (memo.containsKey(design)) {
            return memo[design]!!
        }

        var result = false
        if (design.isEmpty()) {
            result = true
        }

        for (t in towels) {
            if (design.startsWith(t) && partition(design.removePrefix(t), towels, memo)) {
                result = true
            }
        }

        memo[design] = result
        return result
    }

    fun part1(input: List<String>): Int {
        val (towels, designs) = parse(input)
        val memo = hashMapOf<String, Boolean>()
        return designs.count { d ->
            partition(d, towels.toList(), memo)
        }
    }

    fun part2(input: List<String>): Long {
        val (towels, designs) = parse(input)

        val memo = hashMapOf<String, Long>()
        var count = 0L
        for (d in designs) {
            count += partition2(d, towels, memo)
        }
        return count

    }

    val testInput = readInput("Day19", true, 1)

    check(part1(testInput) == 6)
    val part2Check = part2(testInput)
    check(part2Check == 16L)

    val input = readInput("Day19", false)
    part1(input).println()
    part2(input).println()
}