import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: String, isTest: Boolean, testNumber: Int = 1) =
    Path("src/$day/${if (isTest) "test$testNumber" else "input"}.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> List<List<T>>.print() {
    for (i in this.indices) {
        if (this[i].isNotEmpty()) {
            for (j in this[i].indices) {
                print("${this[i][j]}")
            }
            kotlin.io.println()
        }
    }
}

fun List<String>.printList() {
    for (i in this.indices) {
        if (this[i].isNotEmpty()) {
            for (j in this[i].indices) {
                print("${this[i][j]}")
            }
            kotlin.io.println()
        }
    }
}

fun Array<CharArray>.print() {
    for (i in this.indices) {
        for (j in this[0].indices) {
            print("${this[i][j]}")
        }
        println()
    }
}

fun Cell.inbounds(input: List<List<Char>>): Boolean {
    val cell = this
    return cell.r >= 0 && cell.r < input.size && cell.c >= 0 && cell.c < input[0].size
}

fun directions(): List<Pair<Int, Int>> {
    return listOf(
        (0 to 1), //right
        (1 to 0), //down
        (0 to -1), //left
        (-1 to 0), //up
    )
}

enum class Direction(val dr: Int, val dc: Int) {
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1),
    UP(-1, 0);

//    private val map = entries.associateBy { it.dr to it.dc }
//    operator fun get(dr: Int, dc: Int) = map[dr to dc]

    companion object {
        fun all() = entries.toList()
        fun fromString(s: String): Direction {
            val (dx, dy) = s.split(",").map { it.toInt() }

            return when {
                dx == 0 && dy == 1 -> RIGHT
                dx == 1 && dy == 0 -> DOWN
                dx == 0 && dy == -1 -> LEFT
                dx == -1 && dy == 0 -> UP
                else -> {
                    throw IllegalArgumentException("Unknown directions")
                }
            }
        }

        fun Direction.clockwise(): Direction {
            return when(this){
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP -> RIGHT
            }
        }

        fun Direction.counterclockwise(): Direction {
            return when(this){
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
                UP -> LEFT
            }
        }

//        fun Direction.clockwise(): Direction {
//            return this[dc, -dr]!!
//        }
//
//        fun Direction.counterclockwise(): Direction {
//            return this[-dc, dc]!!
//        }
    }
}


