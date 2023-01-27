package dfa

fun <A> emptyDFA(alphabet: Set<A>) = DFAImpl(
    alphabet = alphabet,
    states = setOf(0),
    startState = 0,
    finalStates = emptySet(),
    transitions = listOf(emptyMap())
)
