package dfa

private val SINGLE_STATE = 0

fun <A> anyStringDFA(alphabet: Set<A>) = DFAImpl(
    alphabet = alphabet,
    states = setOf(SINGLE_STATE),
    startState = SINGLE_STATE,
    finalStates = setOf(SINGLE_STATE),
    transitions = listOf(alphabet.associateWith { SINGLE_STATE })
)
