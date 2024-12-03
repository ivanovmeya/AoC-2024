fun main() {
    fun perform(realInput: String): Long {
        val operationRegex = Regex("""mul[(]\d{1,3},\d{1,3}[)]""")
        val digitRegex = Regex("""\d{1,3}""")
        val matchResults = operationRegex.findAll(realInput)

        var sum = 0L
        for (match in matchResults) {
            var mul = 1L
            val digits = digitRegex.findAll(match.value)
            for (digitMatch in digits) {
                mul *= digitMatch.value.toLong()
            }
            sum += mul
        }

        return sum
    }

    fun cutDonts(input: String): String {
        val r = Regex("""don\'t[(][)].*?do[(][)]""")
        return r.replace(input, "")
    }

    fun part1(input: List<String>): Long {
        val realInput = input.joinToString()
        return perform(realInput)
    }

    fun part2(input: List<String>): Long {
        val realInput = input.joinToString()
        val fixedInput = cutDonts(realInput)
        return perform(fixedInput)
    }

    val testInput = readInput("Day03", true, 1)
    check(part1(testInput) == 161L)

    val testInput2 = readInput("Day03", true, 2)
    check(part2(testInput2) == 48L)

    val input = readInput("Day03", false)
    part1(input).println()
    part2(input).println()
}