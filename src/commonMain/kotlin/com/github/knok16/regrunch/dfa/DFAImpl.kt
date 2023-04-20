package com.github.knok16.regrunch.dfa

import com.github.knok16.regrunch.State

class DFAImpl<A> internal constructor(
    override val alphabet: Set<A>,
    override val states: Set<State>,
    override val startState: State,
    override val finalStates: Set<State>,
    private val transitions: List<Map<A, State>>
) : DFA<A, State> {
    override fun transition(state: State, symbol: A): State =
        transitions[state][symbol]
            ?: throw IllegalArgumentException("There is no transition from state $state on symbol '$symbol'")
}
