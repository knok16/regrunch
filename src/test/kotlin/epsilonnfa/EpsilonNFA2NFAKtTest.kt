package epsilonnfa

import epsilonnfa.Fixtures.example1
import epsilonnfa.Fixtures.example2
import nfa.accept
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EpsilonNFA2NFAKtTest {
    @Test
    fun simpleTest() {
        val nfa = example1.toNFA()
        assertFalse(nfa.accept(""))

        assertTrue(nfa.accept("0"))
        assertTrue(nfa.accept("1"))

        assertFalse(nfa.accept("00"))
        assertTrue(nfa.accept("01"))
        assertFalse(nfa.accept("10"))
        assertFalse(nfa.accept("11"))

        assertTrue(nfa.accept("000"))
        assertFalse(nfa.accept("001"))
        assertFalse(nfa.accept("010"))
        assertTrue(nfa.accept("011"))
        assertFalse(nfa.accept("100"))
        assertFalse(nfa.accept("101"))
        assertFalse(nfa.accept("110"))
        assertTrue(nfa.accept("111"))

        assertFalse(nfa.accept("0000"))
        assertFalse(nfa.accept("0001"))
        assertFalse(nfa.accept("0010"))
        assertFalse(nfa.accept("0011"))
        assertFalse(nfa.accept("0100"))
        assertFalse(nfa.accept("0101"))
        assertFalse(nfa.accept("0110"))
        assertFalse(nfa.accept("0111"))
        assertFalse(nfa.accept("1000"))
        assertFalse(nfa.accept("1001"))
        assertFalse(nfa.accept("1010"))
        assertFalse(nfa.accept("1011"))
        assertFalse(nfa.accept("1100"))
        assertFalse(nfa.accept("1101"))
        assertFalse(nfa.accept("1110"))
        assertFalse(nfa.accept("1111"))
    }

    @Test
    fun example2() {
        val nfa = example2.toNFA()
        assertTrue(nfa.accept(""))
        assertTrue(nfa.accept("ab"))
        assertTrue(nfa.accept("aba"))
        assertTrue(nfa.accept("ababab"))
        assertTrue(nfa.accept("abaabaaba"))

        assertFalse(nfa.accept("a"))
        assertFalse(nfa.accept("b"))
        assertFalse(nfa.accept("ba"))
        assertFalse(nfa.accept("abb"))
        assertFalse(nfa.accept("aaa"))
        assertFalse(nfa.accept("aab"))
        assertFalse(nfa.accept("bab"))
        assertFalse(nfa.accept("ababaaa"))
    }
}