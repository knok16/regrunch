package dfa

import State

fun DFA<Char, State>.strings(upTo: Int? = null): List<String> {
    val result = ArrayList<String>()

    val deadStates = deadStates()
    val alphabetSorted = alphabet.sorted()

    fun dfs(prefix: String, state: State) {
        if (state in deadStates || upTo != null && prefix.length > upTo) return
        if (state in finalStates) {
            result.add(prefix)
        }

        alphabetSorted.forEach { symbol ->
            dfs(prefix + symbol, transition(state, symbol))
        }
    }

    dfs("", startState)

    return result
}
