package dfa

fun <A> emptyDFA() = DFAImpl<A>(
    startState = 0,
    finalStates = emptySet(),
    transitions = listOf(emptyMap())
)
