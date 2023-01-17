package dfa

class DFABuilder<A> {
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

    fun build(): DFA<A> = DFA(
        startState = 0,
        finalStates = finalStates.toSet(),
        transitions = transitions
    )
}

fun <A> dfa(buildFunction: DFABuilder<A>.() -> Unit): DFA<A> =
    DFABuilder<A>().also { it.buildFunction() }.build()