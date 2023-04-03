package dfa

import State

class DFABuilder<A>(
    private val alphabet: Set<A>
) {
    private val transitions: MutableList<MutableMap<A, State>> = ArrayList()
    private val finalStates: MutableSet<State> = HashSet()
    val startState = newState()

    fun newState(): State {
        transitions.add(HashMap())
        return transitions.lastIndex
    }

    fun transition(from: State, to: State, on: A) {
        val trans = transitions[from]
        if (on in trans) throw IllegalArgumentException("Transition on '$on' already defined for state #$from")
        trans[on] = to
    }

    fun markAsFinal(state: State) {
        finalStates.add(state)
    }

    fun build(): DFA<A, State> {
        // TODO improve API
        val deadState = newState()
        for (transitionsFromState in transitions)
            for (symbol in alphabet)
                if (symbol !in transitionsFromState)
                    transitionsFromState[symbol] = deadState

//        if (deadState.isInitialized())
//            for (symbol in alphabet)
//                transitions[deadState.value][symbol] = deadState.value

        return DFAImpl(
            alphabet = alphabet,
            states = (0 until transitions.size).toSet(),
            startState = 0,
            finalStates = finalStates.toSet(),
            transitions = transitions
        )
    }
}

fun <A> dfa(alphabet: Set<A>, buildFunction: DFABuilder<A>.() -> Unit): DFA<A, State> =
    DFABuilder(alphabet).also { it.buildFunction() }.build()

fun <A> DFABuilder<A>.newFinalState(): State =
    newState().also { markAsFinal(it) }
