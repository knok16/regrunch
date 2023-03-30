package regex

import epsilonnfa.epsilonNFA
import epsilonnfa.newFinalState
import epsilonnfa.transition
import kotlin.test.Test
import kotlin.test.assertEquals

private fun concatenation(vararg parts: RegexPart) = Concatenation(parts.toList())
private fun union(vararg parts: RegexPart) = Union(parts.toSet())

class RegexToEpsilonNFAKtTest {
    private val asciiAlphabet = (0..127).map { it.toChar() }.toSet()

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
    fun nonStandardAlphabet() {
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
}