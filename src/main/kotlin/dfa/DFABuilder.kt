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

    fun build(): DFA<A, State> = cutStatesThatDoNotLeadToFinalStates(
        DFAImpl(
            alphabet = alphabet,
            states = (0 until transitions.size).toSet(),
            startState = 0,
            finalStates = finalStates.toSet(),
            transitions = transitions
        )
    )
}

fun <A> dfa(alphabet: Set<A>, buildFunction: DFABuilder<A>.() -> Unit): DFA<A, State> =
    DFABuilder<A>(alphabet).also { it.buildFunction() }.build()

// Cut states that do not lead to final state
private fun <A> cutStatesThatDoNotLeadToFinalStates(dfa: DFA<A, State>): DFA<A, State> {
    val reverseEdges = Array(dfa.states.size) { HashSet<State>() }
    (0 until dfa.states.size).forEach { state ->
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
        dfa.startState !in visited -> emptyDFA(dfa.alphabet)
        visited.size == dfa.states.size -> dfa
        else -> {
            val renumbering = visited
                .sorted()
                .withIndex()
                .associate { it.value to it.index }

            DFAImpl(
                dfa.alphabet,
                (0 until visited.size).toSet(),
                0,
                dfa.finalStates.map(renumbering::getValue).toSet(),
                (0 until dfa.states.size)
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
