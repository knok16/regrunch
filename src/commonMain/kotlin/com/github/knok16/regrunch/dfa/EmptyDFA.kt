package com.github.knok16.regrunch.dfa

private val SINGLE_STATE = 0

fun <A> emptyDFA(alphabet: Set<A>) = DFAImpl(
    alphabet = alphabet,
    states = setOf(SINGLE_STATE),
    startState = SINGLE_STATE,
    finalStates = emptySet(),
    transitions = listOf(alphabet.associateWith { SINGLE_STATE })
)
