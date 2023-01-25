package nfa

import State

class NFABuilder<A> {
    private val transitions: MutableList<MutableMap<A, Set<State>>> = ArrayList()
    private val finalStates: MutableSet<State> = HashSet()
    val startState = newState()

    fun newState(): State {
        transitions.add(HashMap())
        return transitions.lastIndex
    }

    fun transition(from: State, to: Set<State>, on: A) {
        val trans = transitions[from]
        if (on in trans) throw IllegalArgumentException("Transition on '$on' already defined for state #$from")
        trans[on] = to
    }

    fun markAsFinal(state: State) {
        finalStates.add(state)
    }

    fun build(): NFA<A, State> = NFAImpl(
        startState = 0,
        finalStates = finalStates.toSet(),
        transitions = transitions
    )
}

fun <A> nfa(buildFunction: NFABuilder<A>.() -> Unit): NFA<A, State> =
    NFABuilder<A>().also { it.buildFunction() }.build()
