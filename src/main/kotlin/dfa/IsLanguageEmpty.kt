package dfa

fun <A, S> isLanguageEmpty(dfa: DFA<A, S>): Boolean {
    val visited = HashSet<S>()

    fun dfs(state: S): Boolean {
        if (state in dfa.finalStates) return true

        if (!visited.add(state)) return false

        return dfa.alphabet.any { symbol -> dfs(dfa.transition(state, symbol)) }
    }

    return !dfs(dfa.startState)
}