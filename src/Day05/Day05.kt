import java.util.*

fun main() {

    fun parse(input: List<String>): Pair<HashMap<Int, MutableList<Int>>, List<List<Int>>> {

        val rules = hashMapOf<Int, MutableList<Int>>()
        val updates = mutableListOf<List<Int>>()
        input.forEach { line ->
            if (line.contains("|")) {
                //rule
                val (x, y) = line.split("|").map { it.trim().toInt() }
                if (rules.contains(x)) {
                    rules[x]!!.add(y)
                } else {
                    rules[x] = mutableListOf(y)
                }

            } else if (line.contains(",")) {
                //page update
                updates.add(line.split(",").map { it.toInt() })
            }
        }

        return rules to updates
    }

    fun parse2(input: List<String>): List<List<Int>> {

        val rules = mutableListOf<List<Int>>()
        input.forEach { line ->
            if (line.contains("|")) {
                //rule
                val (x, y) = line.split("|").map { it.trim().toInt() }
                rules.add(listOf(y, x))

            }
        }

        return rules
    }

    fun findIncorrectUpdates(
        updates: List<List<Int>>,
        rules: HashMap<Int, MutableList<Int>>,
        incorrect: Boolean
    ): List<List<Int>> {
        val rightOrderUpdates = updates.filter { update ->
            var violation = false
            update.forEachIndexed { pageIndex, page ->
                rules[page]?.let { rulesForPage ->
                    for (rule in rulesForPage) {
                        val ruleIndex = update.indexOf(rule)
                        if (ruleIndex != -1 && ruleIndex < pageIndex) {
                            violation = true
                            break
                        }
                    }
                }
            }
            if (incorrect) {
                violation
            } else {
                !violation
            }
        }
        return rightOrderUpdates
    }

    fun topologicalSort(update: List<Int>, rules: List<List<Int>>): List<Int> {
        val queue: Queue<Int> = LinkedList()
        val indegree = mutableMapOf<Int, Int>()

        for (page in update) {
            indegree[page] = 0
        }

        val dependantPages = hashMapOf<Int, MutableList<Int>>()

        val rulesForPages = rules.filter { update.contains(it[0]) && update.contains(it[1]) }
        for (rule in rulesForPages) {

            val pageToPlace = rule[0]
            val shouldBeBeforePage = rule[1]

            indegree[pageToPlace] = indegree.getOrDefault(pageToPlace, 0) + 1
            dependantPages[shouldBeBeforePage] = dependantPages.getOrDefault(shouldBeBeforePage, mutableListOf()).apply {
                add(pageToPlace)
            }
        }

        for (page in update) {
            if (indegree[page] == 0) {
                queue.offer(page)
            }
        }

        if (queue.isEmpty()) return emptyList()

        val result = mutableListOf<Int>()
        //queue contains only items with 0 indegree
        while (queue.isNotEmpty()) {

            val course = queue.poll()
            result.add(course)
            val adjacentCourses = dependantPages[course] ?: emptyList()

            for (c in adjacentCourses) {
                indegree[c] = indegree.getOrDefault(c, 0) - 1
                if (indegree[c] == 0) {
                    queue.offer(c)
                }
            }
        }

        return result
    }

    fun part1(input: List<String>): Int {
        val (rules, updates) = parse(input)
        val rightOrderUpdates = findIncorrectUpdates(updates, rules, false)
        return rightOrderUpdates.sumOf { update ->
            update[update.size / 2]
        }
    }

    fun part2(input: List<String>): Int {

        val (rules, updates) = parse(input)
        val incorrectUpdates = findIncorrectUpdates(updates, rules, true)

        val prerequisites = parse2(input)

        return incorrectUpdates.sumOf { update ->
            val sortedUpdate = topologicalSort(update, prerequisites)
            sortedUpdate[update.size / 2]
        }
    }

    val testInput = readInput("Day05", true, 1)
    check(part1(testInput) == 143)

    val testInput2 = readInput("Day05", true, 2)
    check(part2(testInput2) == 123)

    val input = readInput("Day05", false)
    part1(input).println()
    part2(input).println()
}