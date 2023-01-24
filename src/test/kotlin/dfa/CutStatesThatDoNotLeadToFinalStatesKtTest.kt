package dfa

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CutStatesThatDoNotLeadToFinalStatesKtTest {
    @Test
    fun cutTentaclesThatDoNotLeadToFinalStates() {
        val dfa = dfa {
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

        assertEquals(4, dfa.transitions.size)
    }

    @Test
    fun cutBlossomsThatDoNotLeadToFinalStates() {
        val dfa = dfa {
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

        assertEquals(4, dfa.transitions.size)
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        val dfa = dfa {
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

        assertEquals(1, dfa.transitions.size)
    }
}