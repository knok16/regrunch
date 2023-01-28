package dfa

fun <A, S> DFA<A, S>.deadStates(): Set<S> {
    val reverseEdges = states.associateWith { HashSet<S>() }
    states.forEach { state ->
        alphabet.forEach { symbol ->
            reverseEdges.getValue(transition(state, symbol)).add(state)
        }
    }

    val visited = HashSet<S>()

    fun dfs(state: S) {
        if (state !in visited) {
            visited.add(state)
            reverseEdges.getValue(state).forEach(::dfs)
        }
    }

    finalStates.forEach(::dfs)

    return states - visited
}