package dfa

import State

fun DFA<Char, State>.strings(upTo: Int? = null): List<String> {
    val result = ArrayList<String>()

    fun dfs(prefix: String, state: State) {
        if (upTo != null && prefix.length > upTo) return
        if (state in finalStates) {
            result.add(prefix)
        }

        transitionsFrom(state).toList().sortedBy { (char, _) -> char }.forEach { (char, to) ->
            dfs(prefix + char, to)
        }
    }

    dfs("", startState)

    return result
}
