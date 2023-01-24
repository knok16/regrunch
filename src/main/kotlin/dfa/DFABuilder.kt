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

    fun build(): DFA<A, State> = cutStatesThatDoNotLeadToFinalStates(
        DFAImpl(
            startState = 0,
            finalStates = finalStates.toSet(),
            transitions = transitions
        )
    )
}

fun <A> dfa(buildFunction: DFABuilder<A>.() -> Unit): DFA<A, State> =
    DFABuilder<A>().also { it.buildFunction() }.build()

// Cut states that do not lead to final state
private fun <A> cutStatesThatDoNotLeadToFinalStates(dfa: DFA<A, State>): DFA<A, State> {
    val reverseEdges = Array(dfa.statesCount) { HashSet<State>() }
    (0 until dfa.statesCount).forEach { state ->
        dfa.transitionsFrom(state).forEach { (_, to) ->
            reverseEdges[to].add(state)
        }
    }

    val visited = HashSet<State>()

    fun dfs(state: State) {
        if (state !in visited) {
            visited.add(state)
            reverseEdges[state].forEach(::dfs)
        }
    }

    dfa.finalStates.forEach(::dfs)

    return when {
        dfa.startState !in visited -> emptyDFA()
        visited.size == dfa.statesCount -> dfa
        else -> {
            val renumbering = visited
                .sorted()
                .withIndex()
                .associate { it.value to it.index }

            DFAImpl(
                0,
                dfa.finalStates.map(renumbering::getValue).toSet(),
                (0 until dfa.statesCount)
                    .filter { it in visited }
                    .map { state ->
                        dfa.transitionsFrom(state)
                            .filterValues { it in renumbering }
                            .mapValues { (_, to) -> renumbering.getValue(to) }
                    }
            )
        }
    }
}