package com.github.knok16.regrunch.dfa

// TODO rename
fun <A> anyStringOfLengthUpTo(alphabet: Set<A>, maxLength: Int) = dfa(alphabet) {
    markAsFinal(startState)
    (1..maxLength).fold(startState) { prev, _ ->
        val next = newFinalState()

        alphabet.forEach { symbol -> transition(prev, next, symbol) }

        next
    }
}