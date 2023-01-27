package epsilonnfa

import State
import nfa.NFA
import nfa.NFABuilder

fun <A> convertToNFA(nfa: EpsilonNFA<A, State>): NFA<A, State> {
    val builder = NFABuilder<A>()

    val n = nfa.states.size
    for (i in 1 until n) builder.newState()

    for (state in 0 until n) {
        val closure = nfa.closure(state)

        for (symbol in nfa.alphabet) {
            builder.transition(state, nfa.transition(closure, symbol), symbol)
        }

        if ((closure intersect nfa.finalStates).isNotEmpty()) builder.markAsFinal(state)
    }

    return builder.build()
}

fun <A> EpsilonNFA<A, State>.toNFA(): NFA<A, State> = convertToNFA(this)