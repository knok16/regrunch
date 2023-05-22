package com.github.knok16.regrunch.regex

import com.github.knok16.regrunch.State
import com.github.knok16.regrunch.epsilonnfa.EpsilonNFA
import com.github.knok16.regrunch.epsilonnfa.EpsilonNFABuilder
import com.github.knok16.regrunch.epsilonnfa.newFinalState
import com.github.knok16.regrunch.epsilonnfa.transition

internal fun convert(
    regexPart: RegexPart,
    builder: EpsilonNFABuilder<Char>,
    alphabetContext: AlphabetContext
): Pair<State, State> {
    return when (regexPart) {
        is Symbol -> {
            val start = builder.newState()
            val finish = builder.newState()

            alphabetContext.convertToChars(regexPart).forEach {
                builder.transition(start, finish, it)
            }
            start to finish
        }

        is Concatenation -> regexPart.parts
            .map { convert(it, builder, alphabetContext) }
            .takeIf { it.isNotEmpty() }
            ?.reduce { (a, b), (c, d) ->
                builder.epsilonTransition(b, c)
                a to d
            }
            ?: run {
                val start = builder.newState()
                val finish = builder.newState()
                builder.epsilonTransition(start, finish)
                start to finish
            }

        is Repeat -> {
            if (regexPart.type == Repeat.Type.POSSESSIVE)
                throw IllegalArgumentException("Possessive quantifiers are not supported")

            val start = builder.newState()
            val finish = builder.newState()

            val minRepeats = (1..regexPart.min).fold(start) { acc, _ ->
                val (a, b) = convert(regexPart.part, builder, alphabetContext)
                builder.epsilonTransition(acc, a)
                b
            }
            builder.epsilonTransition(minRepeats, finish)

            if (regexPart.max != null) {
                ((regexPart.min + 1)..regexPart.max).fold(minRepeats) { acc, _ ->
                    val (a, b) = convert(regexPart.part, builder, alphabetContext)
                    builder.epsilonTransition(acc, a)
                    builder.epsilonTransition(b, finish)
                    b
                }
            } else {
                val (a, b) = convert(regexPart.part, builder, alphabetContext)

                val c = builder.newState()
                val d = builder.newState()

                builder.epsilonTransition(minRepeats, c)
                builder.epsilonTransition(c, a)
                builder.epsilonTransition(b, d)
                builder.epsilonTransition(d, c)
                builder.epsilonTransition(d, finish)
            }

            start to finish
        }

        is Union -> {
            val start = builder.newState()
            val finish = builder.newState()

            regexPart.parts.forEach { part ->
                val (a, b) = convert(part, builder, alphabetContext)

                builder.epsilonTransition(start, a)
                builder.epsilonTransition(b, finish)
            }

            start to finish
        }

        is CaptureGroup -> convert(regexPart.part, builder, alphabetContext)
    }
}

fun RegexPart.toEpsilonNFA(alphabetContext: AlphabetContext): EpsilonNFA<Char, State> {
    val result = EpsilonNFABuilder(alphabetContext.alphabet)

    val (a, b) = convert(this, result, alphabetContext)

    result.epsilonTransition(result.startState, a)
    result.epsilonTransition(b, result.newFinalState())

    return result.build()
}
