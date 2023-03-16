package dfa.product

import State
import dfa.DFA
import dfa.DFABuilder

// TODO rename everything!
fun <A, S1, S2> buildProductDFA(
    dfa1: DFA<A, S1>,
    dfa2: DFA<A, S2>,
    finalStatePredicate: (Pair<S1, S2>) -> Boolean
): DFA<A, State> {
    if (dfa1.alphabet != dfa2.alphabet) throw IllegalArgumentException("DFAs have different alphabets")
    val alphabet = dfa1.alphabet
    val builder = DFABuilder(alphabet)

    val correspondence = HashMap<Pair<S1, S2>, State>()
    correspondence[dfa1.startState to dfa2.startState] = builder.startState

    product(dfa1, dfa2, { statePair ->
        val state = if (statePair.first == dfa1.startState && statePair.second == dfa2.startState)
            builder.startState
        else
            builder.newState()

        correspondence[statePair] = state

        if (finalStatePredicate(statePair))
            builder.markAsFinal(state)
    }, { from, to, on ->
        builder.transition(correspondence.getValue(from), correspondence.getValue(to), on)
    })

    return builder.build()
}