package dfa

import kotlin.test.Test
import kotlin.test.assertEquals

class CutStatesThatDoNotLeadToFinalStatesTest {
    @Test
    fun cutTentaclesThatDoNotLeadToFinalStates() {
        val dfa = dfa(setOf('a', 'b', 'c')) {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'a')
            transition(c, a, 'a')

            // These 3 states can be deleted as it is not possible to reach final state once you get there
            val d = newState()
            val e = newState()
            val f = newState()

            transition(b, d, 'b')
            transition(d, e, 'b')
            transition(e, f, 'b')

            val final = newState()
            markAsFinal(final)

            transition(c, final, 'c')
        }

        assertEquals(4, dfa.states.size)
    }

    @Test
    fun cutBlossomsThatDoNotLeadToFinalStates() {
        val dfa = dfa(setOf('a', 'b', 'c')) {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'a')
            transition(c, a, 'a')

            // These 4 states can be deleted as it is not possible to reach final state once you get there
            val d = newState()
            val e = newState()
            val f = newState()
            val g = newState()

            transition(b, d, 'b')
            transition(d, e, 'b')
            transition(e, f, 'b')
            transition(f, g, 'b')
            transition(g, e, 'b')

            val final = newState()
            markAsFinal(final)

            transition(c, final, 'c')
        }

        assertEquals(4, dfa.states.size)
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        val dfa = dfa(setOf('a', 'b', 'c')) {
            val a = startState
            val b = newState()
            val c = newState()

            transition(a, b, 'a')
            transition(b, c, 'a')
            transition(c, a, 'a')

            val d = newState()
            val e = newState()
            val f = newState()
            val g = newState()

            transition(d, e, 'b')
            transition(e, f, 'b')
            transition(f, g, 'b')
            transition(g, e, 'b')

            val final = newState()
            markAsFinal(final)

            transition(e, final, 'c')
            transition(final, a, 'c')
        }

        assertEquals(1, dfa.states.size)
    }
}
