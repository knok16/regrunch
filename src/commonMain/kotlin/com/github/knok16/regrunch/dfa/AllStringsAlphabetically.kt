package com.github.knok16.regrunch.dfa

import com.github.knok16.regrunch.utils.Stack
import com.github.knok16.regrunch.utils.isNotEmpty

fun <S> DFA<Char, S>.allStringsAlphabetically(): Sequence<String> = sequence {
    val callstack = Stack<Iterator<Pair<Char, S>>>()

    var prefix = CharArray(100)
    val deadStates = deadStates()
    val alphabetSorted = alphabet.sorted()
    val viableTransitions = mutableMapOf<S, List<Pair<Char, S>>>()

    fun getIteratorFor(state: S): Iterator<Pair<Char, S>> =
        viableTransitions.getOrPut(state) {
            alphabetSorted
                .map { symbol -> symbol to transition(state, symbol) }
                .filter { (_, nextState) -> nextState !in deadStates }
        }.iterator()

    callstack.push(getIteratorFor(startState))

    if (startState in finalStates) yield("")

    while (callstack.isNotEmpty()) {
        val stackFrame = callstack.peek()
        if (!stackFrame.hasNext()) {
            callstack.pop()
        } else {
            val (char, state) = stackFrame.next()

            if (callstack.size > prefix.size) {
                prefix = prefix.copyOf(prefix.size * 2)
            }
            prefix[callstack.size - 1] = char

            if (state in finalStates) {
                yield(prefix.concatToString(endIndex = callstack.size))
            }

            callstack.push(getIteratorFor(state))
        }
    }
}

fun <S> DFA<Char, S>.allStringsAlphabetically(maxLength: Int): Sequence<String> =
    (this intersect anyStringOfLengthUpTo(alphabet, maxLength)).allStringsAlphabetically()
