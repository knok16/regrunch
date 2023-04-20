package com.github.knok16.regrunch.epsilonnfa

interface EpsilonNFA<A, S> {
    val alphabet: Set<A>
    val states: Set<S>
    val startState: S
    val finalStates: Set<S>
    fun transition(state: S, symbol: A): Set<S>
    fun epsilonTransitions(state: S): Set<S>
    fun accept(str: Sequence<A>): Boolean
}

fun <A, S> EpsilonNFA<A, S>.transition(states: Set<S>, symbol: A): Set<S> =
    states.flatMap { transition(it, symbol) }.toSet()

fun <A, S> EpsilonNFA<A, S>.accept(str: List<A>): Boolean = accept(str.asSequence())

fun <S> EpsilonNFA<Char, S>.accept(str: String): Boolean = accept(str.asSequence())

fun <A, S> EpsilonNFA<A, S>.closure(state: S): Set<S> {
    val result = HashSet<S>()

    fun dfs(state: S) {
        if (result.add(state)) {
            epsilonTransitions(state).forEach(::dfs)
        }
    }

    dfs(state)

    return result
}
