import java.math.BigInteger

fun main() {

    fun parse(input: List<String>): MutableList<Int> {
        var id = 0
        val memory = mutableListOf<Int>()

        input[0].toList().windowed(2, 2, true) { pair ->
            val fileBlockSize = pair[0].digitToInt()
            repeat(fileBlockSize) {
                memory.add(id)
            }
            id++

            if (pair.size > 1) {
                val emptyBlockSize = pair[1].digitToInt()
                repeat(emptyBlockSize) {
                    memory.add(-1)
                }
            }
        }

        return memory
    }

    fun checkSum(memory: MutableList<Int>): Long {
        var sum = 0L
        memory.forEachIndexed { index, num ->
            val multiplier = if (num != -1) num else 0
            sum += index * multiplier
        }
        return sum
    }


    fun part1(input: List<String>): Long {

        val memory = parse(input)

        //moving
        var end = memory.lastIndex
        var emptyIndex = memory.indexOfFirst { it == -1 }
        while (emptyIndex <= end && emptyIndex != -1) {
            //perform moving from the end to empty spaces
            memory[emptyIndex] = memory[end]
            memory.removeLast()
            end--
            while (emptyIndex <= end && memory[emptyIndex] != -1) emptyIndex++
        }

        return checkSum(memory).toLong()
    }

    fun firstEmptyBlockFitsFile(memory: List<Int>, fileSize: Int): Int {
        return memory.windowed(fileSize, 1).indexOfFirst { block -> block.all { it == -1 } }
    }

    fun part2(input: List<String>): Long {

        val memory = parse(input)

        var end = memory.lastIndex
        var number = memory.last()

        while (end > 0) {
            //calculate file size.
            var fileSize = 0
            while (end > 0 && memory[end] == number) {
                end--
                fileSize++
            }

            var fileStart = end + 1

            //try to find first empty
            var emptyBlockIndex = firstEmptyBlockFitsFile(memory, fileSize)

            if (emptyBlockIndex != -1 && emptyBlockIndex < fileStart) {
                //Move?
                repeat(fileSize) {
                    memory[emptyBlockIndex] = memory[fileStart]
                    memory[fileStart] = -1
                    emptyBlockIndex++
                    fileStart++
                }
            }

            //continue to next group, skip empty space
            while (end > 0 && (memory[end] == -1 || memory[end] == number)) end--
            number = memory[end]

        }
        val checkSum = checkSum(memory)
        return checkSum
    }

    val testInput = readInput("Day09", true, 1)
    check(part1(testInput) == 1928L)

    val testInput2 = readInput("Day09", true, 2)
    val part2 = part2(testInput2)
    check(part2 == 2858L)

    val input = readInput("Day09", false)
    part1(input).println()
    part2(input).println()
}