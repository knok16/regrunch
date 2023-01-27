package nfa

interface NFA<A, S> {
    val alphabet: Set<A>
    val states: Set<S>
    val startState: S
    val finalStates: Set<S>
    val statesCount: Int
    fun transitionsFrom(state: S): Set<A>
    fun transition(state: S, symbol: A): Set<S>
    fun accept(str: Sequence<A>): Boolean
}

fun <A, S> NFA<A, S>.transition(states: Set<S>, symbol: A): Set<S> =
    states.flatMap { transition(it, symbol) }.toSet()

fun <A, S> NFA<A, S>.accept(str: List<A>): Boolean = accept(str.asSequence())

fun <S> NFA<Char, S>.accept(str: String): Boolean = accept(str.asSequence())
