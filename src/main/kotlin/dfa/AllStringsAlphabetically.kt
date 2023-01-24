package dfa

import java.util.*

fun DFA<Char>.allStringsAlphabetically(): Sequence<String> = sequence {
    val callstack = Stack<Iterator<Pair<Char, State>>>()

    val prefix = CharArray(100)

    // TODO optimize to sort once and lazily
    fun getIteratorFor(state: State): Iterator<Pair<Char, State>> =
        transitions[state].toList().sortedBy { (char, _) -> char }.iterator()

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
