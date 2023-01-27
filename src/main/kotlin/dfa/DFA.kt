package dfa

interface DFA<A, S> {
    val alphabet: Set<A>
    val states: Set<S>
    val startState: S
    val finalStates: Set<S>
    fun accept(str: Sequence<A>): Boolean
    fun transitionsFrom(state: S): Map<A, S>
}

fun <A, S> DFA<A, S>.accept(str: List<A>): Boolean = accept(str.asSequence())

fun <S> DFA<Char, S>.accept(str: String): Boolean = accept(str.asSequence())
