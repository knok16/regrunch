package dfa.product

import dfa.DFA

fun <A, S1, S2> product(
    dfa1: DFA<A, S1>,
    dfa2: DFA<A, S2>,
    newNode: (Pair<S1, S2>) -> Unit,
    newTransition: (Pair<S1, S2>, Pair<S1, S2>, A) -> Unit
) {
    if (dfa1.alphabet != dfa2.alphabet) throw IllegalArgumentException("DFAs have different alphabets")
    val alphabet = dfa1.alphabet

    val visited = HashSet<Pair<S1, S2>>()

    fun dfs(from: Pair<S1, S2>) {
        visited.add(from)

        newNode(from)

        for (symbol in alphabet) {
            val to1 = dfa1.transition(from.first, symbol)
            val to2 = dfa2.transition(from.second, symbol)

            val to = to1 to to2
            if (to !in visited) dfs(to)

            newTransition(from, to, symbol)
        }
    }

    dfs(dfa1.startState to dfa2.startState)
}

