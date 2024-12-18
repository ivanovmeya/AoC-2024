import kotlin.math.pow

fun main() {

    fun parse(input: List<String>): Register {
        val r = Register(
            A = input[0].split(":")[1].trim().toInt(),
            B = input[1].split(":")[1].trim().toInt(),
            C = input[2].split(":")[1].trim().toInt(),
            pointer = 0
        )

//        val program = input[4].split(":")[2].trim().toCharArray().toList().windowed(2, 2).map {
//            it[0].digitToInt() to it[1].digitToInt()
//        }

        return r

    }


    fun valOf(operand: Int, r: Register): Int {
        return when (operand) {
            0, 1, 2, 3 -> operand
            4 -> r.A
            5 -> r.B
            6 -> r.C
            7 -> throw IllegalArgumentException("7 unsupported operand")
            else -> throw IllegalArgumentException("$operand unsupported operand")
        }
    }

    fun execute(opCode: Int, operand: Int, r: Register, out: MutableList<Int>) {
//        println("exec: c=$opCode, op =$operand, pointer = ${r.pointer}, r= $r")
        when (opCode) {
            0 -> {
                //division
                r.A = (r.A / (2.0.pow(valOf(operand, r).toDouble()))).toInt()
                r.pointer+=2
            }
            1 -> {
                r.B = r.B xor operand
                r.pointer+=2
            }
            2 -> {
                r.B  = valOf(operand, r) % 8
                r.pointer+=2
            }
            3 -> {
                if (r.A != 0) {
                    r.pointer = operand
                } else {
                    r.pointer+=2
                }
            }
            4 -> {
                r.B = r.B xor r.C
                r.pointer+=2
            }
            5 -> {
                out.add(valOf(operand, r) % 8)
                r.pointer+=2
            }
            6 -> {
                r.B = (r.A / (2.0.pow(valOf(operand, r).toDouble()))).toInt()
                r.pointer+=2
            }
            7 -> {
                r.C = (r.A / (2.0.pow(valOf(operand, r).toDouble()))).toInt()
                r.pointer+=2
            }
        }
//        println("exec: c=$opCode, op =$operand, pointer = ${r.pointer}, r= $r AFTER")
    }

    fun perform(r: Register, p: List<Int>, out: MutableList<Int>) {

//        println(r)
//        println(p)

        while (r.pointer < p.size) {
            //perform commands

            val opCode = p[r.pointer]
            val operand = p[r.pointer + 1]
            execute(opCode, operand, r, out)
        }
    }

    fun part1(input: List<String>): String {
        val r = parse(input)

        val p = input[4].split(":")[1].trim().split(',').map { it.toInt() }

        val out = mutableListOf<Int>()
        perform(r, p, out)

        return out.joinToString(",")
    }

    fun part2(input: List<String>): Int {
        val r = parse(input)

        val p = input[4].split(":")[1].trim().split(',').map { it.toInt() }

        val out = mutableListOf<Int>()


        var x = 0
        while (out.joinToString(",") != p.joinToString(",")) {
            println("try A = $x ")
            val rCopy = r.copy(A = x)
            perform(rCopy, p, out)
            x++
        }

        return x
    }

    val testInput = readInput("Day17", true, 1)
//    val part1 = part1(testInput)
//    println("part1 test1 = $part1")
//    check(part1 == "4,6,3,5,6,3,5,2,1,0")



    val testInput2 = readInput("Day17", true, 2)

//    check(part2(testInput2) == 117440)


//    val out = mutableListOf<Int>()
//    perform(Register(0,0,9, 0), listOf(2,6), out)
//    println(out)



//    val out = mutableListOf<Int>()
//    perform(Register(10,0,0, 0), listOf(5,0,5,1,5,4), out)
//    println(out)

//    val out2 = mutableListOf<Int>()
//    perform(Register(2024,0,0, 0), listOf(0,1,5,4,3,0), out2)
//    println(out2)

//    val out3 = mutableListOf<Int>()
//    val r3 = Register(0, 29, 0, 0)
//    perform(r3, listOf(1,7), out3)
//    println(r3)
//    println(out3)

//    val out34 = mutableListOf<Int>()
//    val r = Register(0,2024,43690, 0)
//    perform(r, listOf(4,0), out34)
//    println(r)


    val input = readInput("Day17", false)
//    part1(input).println()
    part2(input).println()
}

data class Register(var A: Int, var B: Int, var C: Int, var pointer: Int)

data class Program(val operations: List<Pair<Int, Int>>)