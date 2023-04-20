package com.github.knok16.regrunch.regex

import com.github.knok16.regrunch.epsilonnfa.epsilonNFA
import com.github.knok16.regrunch.epsilonnfa.newFinalState
import com.github.knok16.regrunch.epsilonnfa.transition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun concatenation(vararg parts: RegexPart) = Concatenation(parts.toList())
private fun union(vararg parts: RegexPart) = Union(parts.toSet())

class RegexToEpsilonNFAKtTest {
    private val asciiAlphabet = (0..0x7F).map { it.toChar() }.toSet()

    @Test
    fun isWordChar() {
        assertFalse('\t'.isWordChar())
        assertFalse(' '.isWordChar())
        assertFalse('!'.isWordChar())
        assertFalse('('.isWordChar())
        assertFalse('*'.isWordChar())
        assertFalse('-'.isWordChar())
        assertFalse('.'.isWordChar())
        assertFalse('/'.isWordChar())
        assertFalse(';'.isWordChar())
        assertFalse(':'.isWordChar())
        assertFalse('<'.isWordChar())
        assertFalse('='.isWordChar())
        assertFalse('>'.isWordChar())
        assertFalse('?'.isWordChar())
        assertFalse('@'.isWordChar())
        assertFalse('['.isWordChar())
        assertFalse('\\'.isWordChar())
        assertFalse(']'.isWordChar())
        assertFalse('^'.isWordChar())
        assertFalse('`'.isWordChar())
        assertFalse('{'.isWordChar())
        assertFalse('|'.isWordChar())
        assertFalse('}'.isWordChar())
        assertFalse('~'.isWordChar())

        assertTrue('1'.isWordChar())
        assertTrue('5'.isWordChar())
        assertTrue('0'.isWordChar())
        assertTrue('A'.isWordChar())
        assertTrue('G'.isWordChar())
        assertTrue('R'.isWordChar())
        assertTrue('S'.isWordChar())
        assertTrue('Z'.isWordChar())
        assertTrue('_'.isWordChar())
        assertTrue('a'.isWordChar())
        assertTrue('f'.isWordChar())
        assertTrue('k'.isWordChar())
        assertTrue('u'.isWordChar())

        assertTrue('ა'.isWordChar())
        assertTrue('ბ'.isWordChar())
        assertTrue('გ'.isWordChar())
        assertTrue('ზ'.isWordChar())
        assertTrue('ჯ'.isWordChar())

        assertTrue('١'.isWordChar())
        assertTrue('٢'.isWordChar())
        assertTrue('٣'.isWordChar())
        assertTrue('٤'.isWordChar())
        assertTrue('٥'.isWordChar())
    }

    @Test
    fun singleSymbol() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                transition(a, b, 'a')

                epsilonTransition(b, newFinalState())
            },
            ExactSymbol('a').toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun digitSymbol() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                for (digit in '0'..'9')
                    transition(a, b, digit)

                epsilonTransition(b, newFinalState())
            },
            DigitSymbol.toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun wordSymbol() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            WordSymbol.toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun anySymbol() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()

                for (digit in asciiAlphabet.filter { it != '\r' && it != '\n' })
                    transition(a, b, digit)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            AnySymbol.toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun verticalWhitespaceSymbol() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()

                for (symbol in setOf('\n', 0x0B.toChar(), 0x0C.toChar(), '\r'))
                    transition(a, b, symbol)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            VerticalWhitespaceSymbol.toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun horizontalWhitespaceSymbol() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()

                for (symbol in setOf('\t', ' '))
                    transition(a, b, symbol)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            HorizontalWhitespaceSymbol.toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun concatenation() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun emptyConcatenation() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()
                epsilonTransition(a, b)

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            Concatenation(emptyList()).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun union() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun emptyUnion() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
                val a = newState()
                val b = newState()

                epsilonTransition(startState, a)
                epsilonTransition(b, newFinalState())
            },
            Union(emptySet()).toEpsilonNFA(asciiAlphabet)
        )
    }


    @Test
    fun optional() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun exactRepeats() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun rangeRepeat() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun repeatAtLeast2() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
        )
    }

    @Test
    fun kleeneStar() {
        assertEquals(
            epsilonNFA(asciiAlphabet) {
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
            ).toEpsilonNFA(asciiAlphabet)
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
            WordSymbol.toEpsilonNFA(alphabet)
        )
    }

    @Test
    fun nonStandardAlphabet2() {
        val alphabet = asciiAlphabet + setOf('١', '٢', '٣', '٤', '٥')

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
            DigitSymbol.toEpsilonNFA(alphabet)
        )
    }
}