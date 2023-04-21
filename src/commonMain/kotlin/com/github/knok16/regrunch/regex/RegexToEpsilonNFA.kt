package com.github.knok16.regrunch.regex

import com.github.knok16.regrunch.State
import com.github.knok16.regrunch.epsilonnfa.EpsilonNFA
import com.github.knok16.regrunch.epsilonnfa.EpsilonNFABuilder
import com.github.knok16.regrunch.epsilonnfa.newFinalState
import com.github.knok16.regrunch.epsilonnfa.transition
import kotlin.text.CharCategory.SPACE_SEPARATOR

internal fun Char.isWordChar(): Boolean = category in setOf(
    CharCategory.LOWERCASE_LETTER,
    CharCategory.UPPERCASE_LETTER,
    CharCategory.TITLECASE_LETTER,
    CharCategory.OTHER_LETTER,
    CharCategory.MODIFIER_LETTER,
    CharCategory.NON_SPACING_MARK,
    CharCategory.DECIMAL_DIGIT_NUMBER,
    CharCategory.CONNECTOR_PUNCTUATION
)

// TODO move .toSet() at the end? Any performance ramifications?
internal fun Symbol.convertToChars(alphabet: Set<Char>): Set<Char> = when (this) {
    is ExactSymbol -> setOf(value)
    is AnySymbol -> alphabet - setOf(
        '\n',           // Line Feed
        '\r',           // Carriage Return
        0x85.toChar(),  // Next Line
        0x2028.toChar(),// Line Separator
        0x2029.toChar() // Paragraph Separator
    )

    is DigitSymbol -> alphabet.filter { it.isDigit() }.toSet()
    is NonDigitSymbol -> alphabet.filterNot { it.isDigit() }.toSet()
    is WhitespaceSymbol -> alphabet.filter { it.isWhitespace() }.toSet()
    is NonWhitespaceSymbol -> alphabet.filterNot { it.isWhitespace() }.toSet()
    is VerticalWhitespaceSymbol -> alphabet intersect setOf(
        '\n',           // Line Feed
        0x0B.toChar(),  // Vertical Tab
        0x0C.toChar(),  // Form Feed
        '\r',           // Carriage Return
        0x85.toChar(),  // Next Line
        0x2028.toChar(),// Line Separator
        0x2029.toChar() // Paragraph Separator
    )

    is HorizontalWhitespaceSymbol -> alphabet.filter { it == '\t' || it.category == SPACE_SEPARATOR }.toSet()
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

        is CaptureGroup -> convert(regexPart.part, builder, alphabet)
    }
}

fun RegexPart.toEpsilonNFA(alphabet: Set<Char>): EpsilonNFA<Char, State> {
    val result = EpsilonNFABuilder(alphabet)

    val (a, b) = convert(this, result, alphabet)

    result.epsilonTransition(result.startState, a)
    result.epsilonTransition(b, result.newFinalState())

    return result.build()
}
