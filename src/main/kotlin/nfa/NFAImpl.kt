package nfa

import State

class NFAImpl<A> internal constructor(
    override val startState: State,
    override val finalStates: Set<State>,
    private val transitions: List<Map<A, Set<State>>>
) : NFA<A, State> {
    override fun accept(str: Sequence<A>): Boolean =
        (str.fold(setOf(startState)) { states, symbol ->
            states.flatMap { transition(it, symbol) }.toSet()
        } intersect finalStates).isNotEmpty()

    override fun transitionsFrom(state: State): Set<A> =
        transitions[state].keys

    override val statesCount: Int
        get() = transitions.size

    override fun transition(state: State, symbol: A): Set<State> =
        transitions[state][symbol] ?: emptySet()
}
