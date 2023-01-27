package epsilonnfa

import State

class EpsilonNFABuilder<A>(
    private val alphabet: Set<A>
) {
    private val transitions: MutableList<MutableMap<A, Set<State>>> = ArrayList()
    private val epsilonTransitions: MutableList<MutableSet<State>> = ArrayList()
    private val finalStates: MutableSet<State> = HashSet()
    val startState = newState()

    fun newState(): State {
        transitions.add(HashMap())
        epsilonTransitions.add(HashSet())
        return transitions.lastIndex
    }

    fun transition(from: State, to: Set<State>, on: A) {
        val trans = transitions[from]
        if (on in trans) throw IllegalArgumentException("Transition on '$on' already defined for state #$from")
        trans[on] = to
    }

    fun epsilonTransition(from: State, to: State) {
        // TODO add validation that it was not added before?
        epsilonTransitions[from].add(to)
    }

    fun markAsFinal(state: State) {
        finalStates.add(state)
    }

    fun build(): EpsilonNFA<A, State> = EpsilonNFAImpl(
        alphabet = alphabet,
        states = (0 until transitions.size).toSet(),
        startState = 0,
        finalStates = finalStates.toSet(),
        transitions = transitions,
        epsilonTransitions = epsilonTransitions
    )
}

fun <A> epsilonNFA(alphabet: Set<A>, buildFunction: EpsilonNFABuilder<A>.() -> Unit): EpsilonNFA<A, State> =
    EpsilonNFABuilder(alphabet).also { it.buildFunction() }.build()
