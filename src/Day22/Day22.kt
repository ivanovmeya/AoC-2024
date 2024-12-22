import kotlin.math.max

fun main() {

    fun next(secret: Long): Long {
        var res = ((secret shl 6) xor secret) % 16777216
        res = ((res shr 5) xor res) % 16777216
        return ((res shl 11) xor res) % 16777216
    }

    fun part1(input: List<String>): Long {

        val secrets = input.map { it.toLong() }

        return secrets.sumOf { secret ->
            var current = secret
            repeat(2000) {
                current = next(current)
            }
            current
        }
    }

    fun part2(input: List<String>): Int {
        val secrets = input.map { it.toLong() }

        val simulations = 2000

        val buyers = secrets.map { secret ->
            var current = secret
            val digitsWithChange = (1..simulations).map {
                val prevLastDigit = current.toString().toCharArray().last().digitToInt()
                current = next(current)
                val lastDigit = current.toString().toCharArray().last().digitToInt()
                val change = lastDigit - prevLastDigit
                lastDigit to change
            }
            digitsWithChange
        }

        //hash set of sequences of changes of 4
        val existedSeq = hashSetOf<String>()
        val seqToBestCost = hashMapOf<Pair<Int, String>, Int>()

        for (i in buyers.indices) {
            val buyer = buyers[i]
            buyer.windowed(4, 1) { digitsWithChange ->
                val seq = digitsWithChange.map { it.second }.toList()
                existedSeq.add(seq.joinToString(""))

                val price = digitsWithChange.last().first

                val key = i to seq.joinToString("")
                if (!seqToBestCost.containsKey(key)) {
                    seqToBestCost[key] = price
                }
            }
        }

        var maximum = 0

        for (seq in existedSeq) {
            var totalSumOfBuyers = 0
            for (i in buyers.indices) {
                totalSumOfBuyers += seqToBestCost.getOrDefault(i to seq, 0)
            }

            if (totalSumOfBuyers > maximum) {
                maximum = totalSumOfBuyers
            }
            maximum = max(maximum, totalSumOfBuyers)
        }

        return maximum
    }

    val testInput = readInput("Day22", true, 1)
    val testInput2 = readInput("Day22", true, 2)

    check(part1(testInput) == 37327623L)
    check(part2(testInput2) == 23)

    val input = readInput("Day22", false)
    part1(input).println()
    part2(input).println()
}