package dfa

import java.util.*

/**
 * Returns sequence of strings accepted by Deterministic Finite Automaton
 * in order from shortest to longest strings,
 * strings of the same lengths sorted alphabetically.
 */
fun <S> DFA<Char, S>.allStrings(): Sequence<String> = sequence {
    val deadStates = deadStates()
    val alphabetSorted = alphabet.sorted()

    val queue: Queue<Pair<S, String>> = LinkedList()

    if (startState in finalStates) {
        yield("")
    }
    queue.add(startState to "")

    while (queue.isNotEmpty()) {
        val (state, prefix) = queue.remove()

        for (symbol in alphabetSorted) {
            when (val nextState = transition(state, symbol)) {
                in finalStates -> {
                    val str = prefix + symbol
                    yield(str)
                    queue.offer(nextState to str)
                }

                !in deadStates -> queue.offer(nextState to prefix + symbol)
            }
        }
    }
}
