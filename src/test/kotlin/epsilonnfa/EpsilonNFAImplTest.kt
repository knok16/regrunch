package epsilonnfa

import epsilonnfa.Fixtures.example1
import epsilonnfa.Fixtures.example2
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EpsilonNFAImplTest {
    @Test
    fun simpleTest() {
        assertFalse(example1.accept(""))

        assertTrue(example1.accept("0"))
        assertTrue(example1.accept("1"))

        assertFalse(example1.accept("00"))
        assertTrue(example1.accept("01"))
        assertFalse(example1.accept("10"))
        assertFalse(example1.accept("11"))

        assertTrue(example1.accept("000"))
        assertFalse(example1.accept("001"))
        assertFalse(example1.accept("010"))
        assertTrue(example1.accept("011"))
        assertFalse(example1.accept("100"))
        assertFalse(example1.accept("101"))
        assertFalse(example1.accept("110"))
        assertTrue(example1.accept("111"))

        assertFalse(example1.accept("0000"))
        assertFalse(example1.accept("0001"))
        assertFalse(example1.accept("0010"))
        assertFalse(example1.accept("0011"))
        assertFalse(example1.accept("0100"))
        assertFalse(example1.accept("0101"))
        assertFalse(example1.accept("0110"))
        assertFalse(example1.accept("0111"))
        assertFalse(example1.accept("1000"))
        assertFalse(example1.accept("1001"))
        assertFalse(example1.accept("1010"))
        assertFalse(example1.accept("1011"))
        assertFalse(example1.accept("1100"))
        assertFalse(example1.accept("1101"))
        assertFalse(example1.accept("1110"))
        assertFalse(example1.accept("1111"))
    }

    @Test
    fun example2() {
        assertTrue(example2.accept(""))
        assertTrue(example2.accept("ab"))
        assertTrue(example2.accept("aba"))
        assertTrue(example2.accept("ababab"))
        assertTrue(example2.accept("abaabaaba"))

        assertFalse(example2.accept("a"))
        assertFalse(example2.accept("b"))
        assertFalse(example2.accept("ba"))
        assertFalse(example2.accept("abb"))
        assertFalse(example2.accept("aaa"))
        assertFalse(example2.accept("aab"))
        assertFalse(example2.accept("bab"))
        assertFalse(example2.accept("ababaaa"))
    }
}