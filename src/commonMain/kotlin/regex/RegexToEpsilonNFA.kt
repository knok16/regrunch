package regex

import State
import epsilonnfa.EpsilonNFA
import epsilonnfa.EpsilonNFABuilder
import epsilonnfa.newFinalState
import epsilonnfa.transition

// TODO add isPunctuation/Connector and isMark/Nonspacing https://learn.microsoft.com/en-us/dotnet/standard/base-types/character-classes-in-regular-expressions#WordCharacter
internal fun Char.isWordChar(): Boolean = isLetter() || isDigit()

// TODO move .toSet() at the end? Any performance ramifications?
internal fun Symbol.convertToChars(alphabet: Set<Char>): Set<Char> = when (this) {
    is ExactSymbol -> setOf(value)
    is AnySymbol -> alphabet // TODO remove new lines (and possible any other characters that do not covered by . in regex)
    is DigitSymbol -> alphabet.filter { it.isDigit() }.toSet()
    is NonDigitSymbol -> alphabet.filterNot { it.isDigit() }.toSet()
    is WhitespaceSymbol -> alphabet.filter { it.isWhitespace() }.toSet()
    is NonWhitespaceSymbol -> alphabet.filterNot { it.isWhitespace() }.toSet()
    is WordSymbol -> alphabet.filter { it.isWordChar() }.toSet()
    is NonWordSymbol -> alphabet.filterNot { it.isWordChar() }.toSet()
    is SetNotationSymbol -> {
        val symbols = symbols.flatMap { it.convertToChars(alphabet) }.toSet()
        if (negated) alphabet - symbols else symbols
    }

    is Anchor -> throw IllegalArgumentException("Anchors are not supported")
}

internal fun convert(regexPart: RegexPart, builder: EpsilonNFABuilder<Char>, alphabet: Set<Char>): Pair<State, State> {
    return when (regexPart) {
        is Symbol -> {
            val start = builder.newState()
            val finish = builder.newState()

            regexPart.convertToChars(alphabet).forEach {
                builder.transition(start, finish, it)
            }
            start to finish
        }

        is Concatenation -> regexPart.parts
            .map { convert(it, builder, alphabet) }
            .reduce { (a, b), (c, d) ->
                builder.epsilonTransition(b, c)
                a to d
            }

        is Repeat -> {
            val start = builder.newState()
            val finish = builder.newState()

            val minRepeats = (1..regexPart.min).fold(start) { acc, _ ->
                val (a, b) = convert(regexPart.part, builder, alphabet)
                builder.epsilonTransition(acc, a)
                b
            }
            builder.epsilonTransition(minRepeats, finish)

            if (regexPart.max != null) {
                ((regexPart.min + 1)..regexPart.max).fold(minRepeats) { acc, _ ->
                    val (a, b) = convert(regexPart.part, builder, alphabet)
                    builder.epsilonTransition(acc, a)
                    builder.epsilonTransition(b, finish)
                    b
                }
            } else {
                val (a, b) = convert(regexPart.part, builder, alphabet)

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
                val (a, b) = convert(part, builder, alphabet)

                builder.epsilonTransition(start, a)
                builder.epsilonTransition(b, finish)
            }

            start to finish
        }
    }
}

fun RegexPart.toEpsilonNFA(alphabet: Set<Char>): EpsilonNFA<Char, State> {
    val result = EpsilonNFABuilder(alphabet)

    val (a, b) = convert(this, result, alphabet)

    result.epsilonTransition(result.startState, a)
    result.epsilonTransition(b, result.newFinalState())

    return result.build()
}