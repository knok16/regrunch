package dfa

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AllStringsAlphabeticallyTest {
    @Test
    fun allStringsAlphabetically1() {
        assertEquals(
            listOf(
                "TAYLOR00!",
                "TAYLOR00#",
                "TAYLOR00$",
                "TAYLOR00%",
                "TAYLOR00&",
                "TAYLOR00(",
                "TAYLOR00)",
                "TAYLOR00*",
                "TAYLOR00@",
                "TAYLOR00^"
            ),
            Fixtures.taylor.allStringsAlphabetically().take(10).toList()
        )
    }

    @Test
    fun allStringsAlphabetically2() {
        assertEquals(
            listOf(
                "0",
                "000",
                "00000",
                "0000000",
                "000000000",
                "00000000000",
                "0000000000000",
                "000000000000000",
                "00000000000000000",
                "0000000000000000000"
            ),
            Fixtures.oddZeros.allStringsAlphabetically().take(10).toList()
        )
    }

    @Test
    fun dfaHasCycleInItsDefinitionThatDoNotLeadToFinalState() {
        assertEquals(
            listOf("b"),
            dfa {
                val a = startState
                val b = newState()
                val c = newState()
                val d = newState()

                transition(a, b, 'a')
                transition(b, c, 'a')
                transition(c, d, 'a')
                transition(d, b, 'a')

                val final = newState()
                markAsFinal(final)

                transition(a, final, 'b')
            }.allStringsAlphabetically().toList()
        )
    }

    @Test
    fun allStringsOfEmptyDFA() {
        assertEquals(
            emptyList(),
            emptyDFA<Char>().allStringsAlphabetically().toList()
        )
    }
}
