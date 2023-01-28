package dfa

import State

fun <A, S1, S2> intersection(dfa1: DFA<A, S1>, dfa2: DFA<A, S2>): DFA<A, State> {
    if (dfa1.alphabet != dfa2.alphabet) throw IllegalArgumentException("DFAs have different alphabets")
    val alphabet = dfa1.alphabet
    val builder = DFABuilder(alphabet)

    val correspondence = HashMap<Pair<S1, S2>, State>()
    correspondence[dfa1.startState to dfa2.startState] = builder.startState

    fun dfs(state1: S1, state2: S2, state: State) {
        for (symbol in alphabet) {
            val to1 = dfa1.transition(state1, symbol)
            val to2 = dfa2.transition(state2, symbol)

            val key = to1 to to2
            val to = if (key in correspondence) {
                correspondence.getValue(key)
            } else {
                val r = builder.newState()
                correspondence[key] = r
                dfs(to1, to2, r)
                r
            }
            builder.transition(state, to, symbol)
        }
    }

    dfs(dfa1.startState, dfa2.startState, builder.startState)

    correspondence
        .filterKeys { (s1, s2) -> s1 in dfa1.finalStates && s2 in dfa2.finalStates }
        .values
        .forEach(builder::markAsFinal)

    return builder.build()
}

infix fun <A, S1, S2> DFA<A, S1>.intersect(another: DFA<A, S2>): DFA<A, State> = intersection(this, another)
