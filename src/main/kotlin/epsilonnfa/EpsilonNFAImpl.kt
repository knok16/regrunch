package epsilonnfa

import State

class EpsilonNFAImpl<A> internal constructor(
    override val startState: State,
    override val finalStates: Set<State>,
    private val transitions: List<Map<A, Set<State>>>,
    private val epsilonTransitions: List<Set<State>>
) : EpsilonNFA<A, State> {
    override fun accept(str: Sequence<A>): Boolean =
        (str.fold(closure(startState)) { states, symbol ->
            transition(states, symbol).flatMap { closure(it) }.toSet()
        } intersect finalStates).isNotEmpty()

    override fun transitionsFrom(state: State): Set<A> =
        transitions[state].keys

    override val statesCount: Int
        get() = transitions.size

    override fun epsilonTransitions(state: State): Set<State> =
        epsilonTransitions[state]

    override fun transition(state: State, symbol: A): Set<State> =
        transitions[state][symbol] ?: emptySet()
}
