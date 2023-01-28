package dfa

import java.util.*

fun <S> DFA<Char, S>.allStringsAlphabetically(): Sequence<String> = sequence {
    val callstack = Stack<Iterator<Pair<Char, S>>>()

    val prefix = CharArray(100)
    val deadStates = deadStates()
    val alphabetSorted = alphabet.sorted()

    fun getIteratorFor(state: S): Iterator<Pair<Char, S>> =
        alphabetSorted
            .map { symbol -> symbol to transition(state, symbol) }
            .filter { (_, nextState) -> nextState !in deadStates }
            .iterator()

    callstack.push(getIteratorFor(startState))

    while (callstack.isNotEmpty()) {
        val stackFrame = callstack.peek()
        if (!stackFrame.hasNext()) {
            callstack.pop()
        } else {
            val (char, state) = stackFrame.next()

            prefix[callstack.lastIndex] = char // TODO resize when needed

            if (state in finalStates) {
                yield(String(prefix, 0, callstack.size))
            }

            callstack.push(getIteratorFor(state))
        }
    }
}
