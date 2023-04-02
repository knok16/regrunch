package dfa

import utils.Stack
import utils.isNotEmpty

fun <S> DFA<Char, S>.allStringsAlphabetically(): Sequence<String> = sequence {
    val callstack = Stack<Iterator<Pair<Char, S>>>()

    var prefix = CharArray(100)
    val deadStates = deadStates()
    val alphabetSorted = alphabet.sorted()

    fun getIteratorFor(state: S): Iterator<Pair<Char, S>> =
        alphabetSorted
            .map { symbol -> symbol to transition(state, symbol) }
            .filter { (_, nextState) -> nextState !in deadStates }
            .iterator()

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
