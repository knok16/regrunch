package com.github.knok16.regrunch.dfa

interface DFA<A, S> {
    val alphabet: Set<A>
    val states: Set<S>
    val startState: S
    val finalStates: Set<S>
    fun transition(state: S, symbol: A): S
}

fun <A, S> DFA<A, S>.accept(str: Sequence<A>): Boolean =
    str.fold(startState, this::transition) in finalStates

fun <A, S> DFA<A, S>.accept(str: List<A>): Boolean = accept(str.asSequence())

fun <S> DFA<Char, S>.accept(str: String): Boolean = accept(str.asSequence())
