package dfa

import kotlin.test.Test
import kotlin.test.assertEquals

class DFAIntersectionTest {
    @Test
    fun intersection1() {
        val dfa3 = Fixtures.divisibilityBy(3)
        val dfa7 = Fixtures.divisibilityBy(7)

        val intersection = dfa3 intersect dfa7

        for (i in 0..10000) {
            assertEquals(i % 21 == 0, intersection.accept(i.toString()))
        }
    }

    @Test
    fun intersection2() {
        val dfa1 = dfa {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'b')
            transition(c, c, 'b')

            markAsFinal(b)
            markAsFinal(c)
        }
        val dfa2 = dfa {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'c')
            transition(c, c, 'c')

            markAsFinal(b)
            markAsFinal(c)
        }

        assertEquals(
            listOf("a"),
            (dfa1 intersect dfa2).allStringsAlphabetically().toList()
        )
    }

    @Test
    fun intersectionWithEmpty() {
        assertEquals(
            emptyList(),
            (Fixtures.taylor intersect emptyDFA()).allStringsAlphabetically().toList()
        )

        assertEquals(
            emptyList(),
            (emptyDFA<Char>() intersect Fixtures.evenOnes).allStringsAlphabetically().toList()
        )
    }
}
