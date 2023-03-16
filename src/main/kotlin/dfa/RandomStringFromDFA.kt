package dfa

import kotlin.random.Random

fun <S> DFA<Char, S>.randomStrings(random: Random = Random.Default): Sequence<String> = sequence {
    val deadStates = deadStates()

    if (startState in deadStates) return@sequence

    while (true) {
        val result = StringBuilder()

        var state = startState
        // TODO rework second part of stopping condition to have more "uniformly" distributed
        while (state !in finalStates || random.nextBoolean()) {
            val possibleTransitions = alphabet
                .map { it to transition(state, it) }
                .filter { (_, newState) -> newState !in deadStates }
            if (possibleTransitions.isEmpty() && state in finalStates) break // TODO beautify
            val (symbol, newState) = possibleTransitions
                .random(random)

            result.append(symbol)
            state = newState
        }

        yield(result.toString())
    }
}
