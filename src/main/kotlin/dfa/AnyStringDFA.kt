package dfa

fun <A> anyStringDFA(alphabet: Set<A>) = DFAImpl(
    alphabet = alphabet,
    states = setOf(0),
    startState = 0,
    finalStates = setOf(0),
    transitions = listOf(alphabet.associateWith { 0 })
)
