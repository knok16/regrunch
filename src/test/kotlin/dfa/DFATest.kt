package dfa

import dfa.Fixtures.divisibilityBy
import dfa.Fixtures.evenOnes
import dfa.Fixtures.taylor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DFATest {
    @Test
    fun accept1() {
        assertTrue(taylor.accept("Taylor12%"))
        assertTrue(taylor.accept("tAyLoR77!"))

        assertFalse(taylor.accept("Taylor123"))
        assertFalse(taylor.accept("tAyLoR77!tAyLoR77!"))
    }

    @Test
    fun accept2() {
        for (i in 0..10000) {
            assertEquals((Integer.bitCount(i) and 1) == 0, evenOnes.accept(i.toString(2)))
        }
    }

    @Test
    fun accept3() {
        for (n in setOf(7, 20, 53, 100, 121)) {
            val dfa = divisibilityBy(n)
            for (i in 0..10000) {
                assertEquals(i % n == 0, dfa.accept(i.toString()))
            }
        }
    }
}