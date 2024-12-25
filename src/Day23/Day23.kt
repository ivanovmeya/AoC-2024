fun main() {

    //adjacency list: node -> adjacent nodes
    fun adjList(input: List<String>): Map<String, Set<String>> {
        val res = mutableMapOf<String, MutableSet<String>>()
        input.map {
            it.split("-")
        }.forEach {
            val (n1, n2) = it
            res[n1] = res.getOrDefault(n1, mutableSetOf()).apply {
                add(n2)
            }

            res[n2] = res.getOrDefault(n2, mutableSetOf()).apply {
                add(n1)
            }
        }
        return res
    }

    //index is from 0 to allNodes.size
    fun buildGroups(
        node: String,
        group: HashSet<String>,
        adjs: Map<String, Set<String>>,
        allGroups: HashSet<List<String>>
    ) {
        //to keep the group all interconnected.
        //check if all group nodes in adjacent list of this node.
        //if not - skip the node. if yes add the node, and continue.
        val key = group.sorted()
        if (allGroups.contains(key)) return
        allGroups.add(key.toList())

        for (adj in adjs[node]!!) {
            if (group.contains(adj)) continue
            if (!adjs[adj]!!.containsAll(group)) continue
            group.add(adj)
            buildGroups(adj, group, adjs, allGroups)
        }
    }

    fun part1(input: List<String>): Int {
        val adjs = adjList(input)

        for (adj in adjs) {
            println(adj)
        }

        println("Total keys = ${adjs.keys.size}")

        val result = hashSetOf<List<String>>()

        for (n1 in adjs.keys) {
            for (n2 in adjs[n1]!!) {
                for (n3 in adjs[n2]!!) {
                    if (n1 != n3 && adjs[n3]!!.contains(n1)) {
                        result.add(listOf(n1, n2, n3).sorted())
                    }
                }
            }
        }

        println("Total groups = ${result.size}")
        val filtered = result.filter { it.any { it.startsWith("t") } }
        return filtered.size
    }

    fun part2(input: List<String>): String {
        val adjs = adjList(input)

        for (adj in adjs) {
            println(adj)
        }

        val result = hashSetOf<List<String>>()
        for (adj in adjs.keys) {

            val group = hashSetOf<String>().apply {
                add(adj)
            }
            buildGroups(adj, group, adjs, result)
        }

        println(result)

        val maxBy = result.maxBy { it.size }
        return maxBy.joinToString(",")
    }

    val testInput = readInput("Day23", true, 1)
    val testInput2 = readInput("Day23", true, 2)

    check(part1(testInput) == 7)
    check(part2(testInput2) == "co,de,ka,ta")

    val input = readInput("Day23", false)
    part1(input).println()
    part2(input).println()
}