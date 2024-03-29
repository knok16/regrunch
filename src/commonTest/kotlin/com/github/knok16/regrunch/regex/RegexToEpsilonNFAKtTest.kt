package com.github.knok16.regrunch.regex

import com.github.knok16.regrunch.epsilonnfa.epsilonNFA
import com.github.knok16.regrunch.epsilonnfa.newFinalState
import com.github.knok16.regrunch.epsilonnfa.transition
import com.github.knok16.regrunch.regex.AlphabetContext.Companion.ASCII
import kotlin.test.*

private fun concatenation(vararg parts: RegexPart) = Concatenation(parts.toList())
private fun union(vararg parts: RegexPart) = Union(parts.toSet())

class RegexToEpsilonNFAKtTest {
    @Test
    fun singleSymbol() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                transition(a, b, 'a')

                epsilonTransition(b, newFinalState())
            },
            ExactSymbol('a').toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun digitSymbol() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                for (digit in '0'..'9')
                    transition(a, b, digit)

                epsilonTransition(b, newFinalState())
            },
            DigitSymbol.toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun wordSymbol() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                for (digit in 'a'..'z')
                    transition(a, b, digit)
                for (digit in 'A'..'Z')
                    transition(a, b, digit)
                for (digit in '0'..'9')
                    transition(a, b, digit)
                transition(a, b, '_')

                epsilonTransition(b, newFinalState())
            },
            WordSymbol.toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun anySymbol() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                for (digit in ASCII.alphabet.filter { it != '\r' && it != '\n' })
                    transition(a, b, digit)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            AnySymbol.toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun verticalWhitespaceSymbol() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                for (symbol in setOf('\n', 0x0B.toChar(), 0x0C.toChar(), '\r'))
                    transition(a, b, symbol)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            VerticalWhitespaceSymbol.toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun horizontalWhitespaceSymbol() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                for (symbol in setOf('\t', ' '))
                    transition(a, b, symbol)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            HorizontalWhitespaceSymbol.toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun concatenation() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()
                transition(a, b, 'a')

                val c = newState()
                val d = newState()
                for (digit in '0'..'9')
                    transition(c, d, digit)

                val e = newState()
                val f = newState()
                transition(e, f, 'b')

                epsilonTransition(startState, a)
                epsilonTransition(b, c)
                epsilonTransition(d, e)
                epsilonTransition(f, newFinalState())
            },
            concatenation(
                ExactSymbol('a'),
                DigitSymbol,
                ExactSymbol('b')
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun emptyConcatenation() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()
                epsilonTransition(a, b)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            Concatenation(emptyList()).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun union() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val g = newState()
                val h = newState()

                val a = newState()
                val b = newState()
                transition(a, b, '1')

                val c = newState()
                val d = newState()
                for (digit in '0'..'9')
                    transition(c, d, digit)

                val e = newState()
                val f = newState()
                transition(e, f, 'b')

                epsilonTransition(g, a)
                epsilonTransition(g, c)
                epsilonTransition(g, e)
                epsilonTransition(b, h)
                epsilonTransition(d, h)
                epsilonTransition(f, h)

                epsilonTransition(startState, g)
                epsilonTransition(h, newFinalState())
            },
            union(
                ExactSymbol('1'),
                DigitSymbol,
                ExactSymbol('b')
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun emptyUnion() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            Union(emptySet()).toEpsilonNFA(ASCII)
        )
    }


    @Test
    fun optional() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val c = newState()
                val d = newState()

                val a = newState()
                val b = newState()
                transition(a, b, 'a')

                epsilonTransition(c, a)
                epsilonTransition(b, d)
                epsilonTransition(c, d)

                epsilonTransition(startState, c)
                epsilonTransition(d, newFinalState())
            },
            Repeat(
                ExactSymbol('a'),
                0,
                1
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun exactRepeats() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val g = newState()
                val h = newState()

                val a = newState()
                val b = newState()
                transition(a, b, 'a')

                val c = newState()
                val d = newState()
                transition(c, d, 'a')

                val e = newState()
                val f = newState()
                transition(e, f, 'a')

                epsilonTransition(g, a)
                epsilonTransition(b, c)
                epsilonTransition(d, e)
                epsilonTransition(f, h)

                epsilonTransition(startState, g)
                epsilonTransition(h, newFinalState())
            },
            Repeat(
                ExactSymbol('a'),
                3,
                3
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun rangeRepeat() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val g = newState()
                val h = newState()

                val a = newState()
                val b = newState()
                transition(a, b, 'a')

                val c = newState()
                val d = newState()
                transition(c, d, 'a')

                val e = newState()
                val f = newState()
                transition(e, f, 'a')

                epsilonTransition(g, a)

                epsilonTransition(b, c)
                epsilonTransition(b, h)

                epsilonTransition(d, e)
                epsilonTransition(d, h)

                epsilonTransition(f, h)

                epsilonTransition(startState, g)
                epsilonTransition(h, newFinalState())
            },
            Repeat(
                ExactSymbol('a'),
                1,
                3
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun repeatAtLeast2() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                val c = newState()
                val d = newState()
                transition(c, d, 'a')

                val e = newState()
                val f = newState()
                transition(e, f, 'a')

                val g = newState()
                val h = newState()
                transition(g, h, 'a')

                val k = newState()
                val l = newState()

                epsilonTransition(a, c)
                epsilonTransition(d, e)
                epsilonTransition(f, b)
                epsilonTransition(f, k)
                epsilonTransition(k, g)
                epsilonTransition(h, l)
                epsilonTransition(l, b)
                epsilonTransition(l, k)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            Repeat(
                ExactSymbol('a'),
                2,
                null
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun kleeneStar() {
        assertEquals(
            epsilonNFA(ASCII.alphabet) {
                val a = newState()
                val b = newState()

                val c = newState()
                val d = newState()
                transition(c, d, 'a')

                val e = newState()
                val f = newState()

                epsilonTransition(a, b)
                epsilonTransition(a, e)
                epsilonTransition(e, c)
                epsilonTransition(d, f)
                epsilonTransition(f, b)
                epsilonTransition(f, e)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            Repeat(
                ExactSymbol('a'),
                0,
                null
            ).toEpsilonNFA(ASCII)
        )
    }

    @Test
    fun nonStandardAlphabet1() {
        val alphabet = setOf('ა', 'ბ', 'გ', 'ზ', ' ')

        assertEquals(
            epsilonNFA(alphabet) {
                val a = newState()
                val b = newState()

                transition(a, setOf(b), 'ა')
                transition(a, setOf(b), 'ბ')
                transition(a, setOf(b), 'გ')
                transition(a, setOf(b), 'ზ')

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            WordSymbol.toEpsilonNFA(AlphabetContext(alphabet))
        )
    }

    @Test
    fun nonStandardAlphabet2() {
        val alphabet = ASCII.alphabet + setOf('١', '٢', '٣', '٤', '٥')

        assertEquals(
            epsilonNFA(alphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                for (digit in '0'..'9')
                    transition(a, b, digit)
                transition(a, b, '١')
                transition(a, b, '٢')
                transition(a, b, '٣')
                transition(a, b, '٤')
                transition(a, b, '٥')

                epsilonTransition(b, newFinalState())
            },
            DigitSymbol.toEpsilonNFA(AlphabetContext(alphabet))
        )
    }

    @Test
    fun anchorsAreNotSupported() {
        assertEquals(
            "Anchors are not supported",
            assertFailsWith<IllegalArgumentException> {
                concatenation(ExactSymbol('a'), WordBoundary, ExactSymbol(' ')).toEpsilonNFA(ASCII)
            }.message
        )
    }

    @Test
    fun possessiveQuantifiersAreNotSupported() {
        assertEquals(
            "Possessive quantifiers are not supported",
            assertFailsWith<IllegalArgumentException> {
                Repeat(ExactSymbol('a'), 2, 5, Repeat.Type.POSSESSIVE).toEpsilonNFA(ASCII)
            }.message
        )
    }
}
