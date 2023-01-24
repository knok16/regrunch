package dfa

fun <A> emptyDFA() = DFA<A>(
    startState = 0,
    finalStates = emptySet(),
    transitions = listOf(emptyMap())
)