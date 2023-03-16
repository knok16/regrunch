package dfa.product

import dfa.Fixtures
import dfa.accept
import dfa.dfa
import kotlin.test.Test
import kotlin.test.assertEquals

class DFAUnionTest {
    @Test
    fun union1() {
        val dfa3 = Fixtures.divisibilityBy(3)
        val dfa7 = Fixtures.divisibilityBy(7)

        val union = union(dfa3, dfa7)

        for (i in 0..10000) {
            assertEquals(i % 3 == 0 || i % 7 == 0, union.accept(i.toString()))
        }
    }

    @Test
    fun union2() {
        val dfa1 = dfa(setOf('a', 'b', 'c')) {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'b')
            transition(c, c, 'b')

            markAsFinal(b)
            markAsFinal(c)
        }
        val dfa2 = dfa(setOf('a', 'b', 'c')) {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'c')
            transition(c, c, 'c')

            markAsFinal(b)
            markAsFinal(c)
        }

//        assertEquals(
//            listOf("a"),
//            (dfa1 + dfa2).allStringsAlphabetically().toList()
//        )
    }

    @Test
    fun unionWithEmpty() {
//        assertEquals(
//            emptyList(),
//            (taylor + emptyDFA(taylor.alphabet)).allStringsAlphabetically().toList()
//        )

//        assertEquals(
//            emptyList(),
//            (emptyDFA(evenOnes.alphabet) + evenOnes).allStringsAlphabetically().toList()
//        )
    }
}
