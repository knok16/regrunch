package com.github.knok16.regrunch.dfa

import com.github.knok16.regrunch.dfa.Fixtures.divisibilityBy
import com.github.knok16.regrunch.dfa.Fixtures.evenOnes
import com.github.knok16.regrunch.dfa.Fixtures.no3Consecutive0
import com.github.knok16.regrunch.dfa.Fixtures.no3Consecutive012
import com.github.knok16.regrunch.dfa.Fixtures.noConsecutiveOnes
import com.github.knok16.regrunch.dfa.Fixtures.taylor
import com.github.knok16.regrunch.dfa.Fixtures.wordEndsInIng
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DFATest {
    @Test
    fun taylor() {
        assertTrue(taylor.accept("Taylor12%"))
        assertTrue(taylor.accept("tAyLoR77!"))

        assertFalse(taylor.accept("Taylor123"))
        assertFalse(taylor.accept("tAyLoR77!tAyLoR77!"))
    }

    private fun bitCount(i: Int): Int = if (i == 0) 0 else (i and 1) + bitCount(i shr 1)

    @Test
    fun evenOnes() {
        for (i in 0..10000) {
            assertEquals((bitCount(i) and 1) == 0, evenOnes.accept(i.toString(2)))
        }
    }

    @Test
    fun divisibilityBy() {
        for (n in setOf(7, 20, 53, 100, 121)) {
            val dfa = divisibilityBy(n)
            for (i in 0..10000) {
                assertEquals(i % n == 0, dfa.accept(i.toString()))
            }
        }
    }

    @Test
    fun noConsecutiveOnesAccepts() {
        assertTrue(noConsecutiveOnes.accept(""))

        assertTrue(noConsecutiveOnes.accept("0"))
        assertTrue(noConsecutiveOnes.accept("1"))

        assertTrue(noConsecutiveOnes.accept("01"))
        assertTrue(noConsecutiveOnes.accept("00"))
        assertTrue(noConsecutiveOnes.accept("10"))

        assertTrue(noConsecutiveOnes.accept("001"))
        assertTrue(noConsecutiveOnes.accept("101"))
        assertTrue(noConsecutiveOnes.accept("010"))
        assertTrue(noConsecutiveOnes.accept("000"))
        assertTrue(noConsecutiveOnes.accept("100"))

        assertTrue(noConsecutiveOnes.accept("0101"))
        assertTrue(noConsecutiveOnes.accept("0001"))
        assertTrue(noConsecutiveOnes.accept("1001"))
        assertTrue(noConsecutiveOnes.accept("0010"))
        assertTrue(noConsecutiveOnes.accept("1010"))
        assertTrue(noConsecutiveOnes.accept("0100"))
        assertTrue(noConsecutiveOnes.accept("0000"))
        assertTrue(noConsecutiveOnes.accept("1000"))

        assertTrue(noConsecutiveOnes.accept("00101"))
        assertTrue(noConsecutiveOnes.accept("10101"))
        assertTrue(noConsecutiveOnes.accept("01001"))
        assertTrue(noConsecutiveOnes.accept("00001"))
        assertTrue(noConsecutiveOnes.accept("10001"))
        assertTrue(noConsecutiveOnes.accept("01010"))
        assertTrue(noConsecutiveOnes.accept("00010"))
        assertTrue(noConsecutiveOnes.accept("10010"))
        assertTrue(noConsecutiveOnes.accept("00100"))
        assertTrue(noConsecutiveOnes.accept("10100"))
        assertTrue(noConsecutiveOnes.accept("01000"))
        assertTrue(noConsecutiveOnes.accept("00000"))
        assertTrue(noConsecutiveOnes.accept("10000"))
    }

    @Test
    fun noConsecutiveOnesDoesNotAccept() {
        assertFalse(noConsecutiveOnes.accept("11"))

        assertFalse(noConsecutiveOnes.accept("110"))
        assertFalse(noConsecutiveOnes.accept("011"))
        assertFalse(noConsecutiveOnes.accept("111"))

        assertFalse(noConsecutiveOnes.accept("0011"))
        assertFalse(noConsecutiveOnes.accept("0110"))
        assertFalse(noConsecutiveOnes.accept("0111"))
        assertFalse(noConsecutiveOnes.accept("1011"))
        assertFalse(noConsecutiveOnes.accept("1100"))
        assertFalse(noConsecutiveOnes.accept("1101"))
        assertFalse(noConsecutiveOnes.accept("1110"))
        assertFalse(noConsecutiveOnes.accept("1111"))

        assertFalse(noConsecutiveOnes.accept("00111"))
        assertFalse(noConsecutiveOnes.accept("11101"))
        assertFalse(noConsecutiveOnes.accept("01111"))
        assertFalse(noConsecutiveOnes.accept("11001"))
        assertFalse(noConsecutiveOnes.accept("01110"))
        assertFalse(noConsecutiveOnes.accept("01100"))
        assertFalse(noConsecutiveOnes.accept("11110"))
        assertFalse(noConsecutiveOnes.accept("11111"))
    }

    @Test
    fun wordEndsInIng() {
        assertTrue(wordEndsInIng.accept("ing"))
        assertTrue(wordEndsInIng.accept("iiiiing"))
        assertTrue(wordEndsInIng.accept("beginning"))
        assertTrue(wordEndsInIng.accept("binging-bing"))
        assertTrue(wordEndsInIng.accept("happening"))
        assertTrue(wordEndsInIng.accept("happenInG"))
        assertTrue(wordEndsInIng.accept("happeniNg"))

        assertFalse(wordEndsInIng.accept("several words appearing"))
        assertFalse(wordEndsInIng.accept("appearing-with-ing-in-the-middle"))
        assertFalse(wordEndsInIng.accept("Torin"))
        assertFalse(wordEndsInIng.accept("aBWini"))
    }

    @Test
    fun no3Consecutive0() {
        assertTrue(no3Consecutive0.accept(""))
        assertTrue(no3Consecutive0.accept("0"))
        assertTrue(no3Consecutive0.accept("00"))
        assertTrue(no3Consecutive0.accept("00100"))
        assertTrue(no3Consecutive0.accept("02100"))
        assertTrue(no3Consecutive0.accept("020102222100"))

        assertFalse(no3Consecutive0.accept("000"))
        assertFalse(no3Consecutive0.accept("000100"))
        assertFalse(no3Consecutive0.accept("021000"))
        assertFalse(no3Consecutive0.accept("02010002222100"))
    }

    @Test
    fun no3Consecutive012() {
        assertTrue(no3Consecutive012.accept(""))
        assertTrue(no3Consecutive012.accept("0"))
        assertTrue(no3Consecutive012.accept("00"))
        assertTrue(no3Consecutive012.accept("00100"))
        assertTrue(no3Consecutive012.accept("02100"))
        assertTrue(no3Consecutive012.accept("0201022100"))

        assertFalse(no3Consecutive012.accept("000"))
        assertFalse(no3Consecutive012.accept("000100"))
        assertFalse(no3Consecutive012.accept("021000"))
        assertFalse(no3Consecutive012.accept("020102222100"))
    }
}
