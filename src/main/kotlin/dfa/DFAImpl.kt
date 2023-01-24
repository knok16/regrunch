package dfa

import State

class DFAImpl<A> internal constructor(
    override val startState: State,
    override val finalStates: Set<State>,
    private val transitions: List<Map<A, State>>
) : DFA<A, State> {
    override fun accept(str: Sequence<A>): Boolean {
        var state = startState
        for (c in str) {
            state = transitions[state][c] ?: return false
        }
        return state in finalStates
    }

    override fun transitionsFrom(state: State): Map<A, State> =
        transitions[state]

    override val statesCount: Int
        get() = transitions.size
}
