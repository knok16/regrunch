package dfa

import dfa.Fixtures.finalStateIsNotReachableFromStart
import dfa.Fixtures.infiniteNumberOfStringsAlphabeticallySmallerThatAny
import dfa.Fixtures.integers
import dfa.Fixtures.no3Consecutive0
import dfa.Fixtures.noConsecutiveOnes
import dfa.Fixtures.oddZeroes
import dfa.Fixtures.singletonEmptyString
import dfa.Fixtures.taylor
import dfa.Fixtures.wordEndsInIng
import kotlin.test.Test
import kotlin.test.assertEquals

class AllStringsKtTest {
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
            taylor.allStrings().take(10).toList()
        )
    }

    @Test
    fun allStringsAlphabetically2() {
        assertEquals(
            listOf(
                "0",
                "01",
                "10",
                "000",
                "011",
                "101",
                "110",
                "0001",
                "0010",
                "0100",
                "0111",
                "1000"
            ),
            oddZeroes.allStrings().take(12).toList()
        )
    }

    @Test
    fun wordEndsInIng() {
        assertEquals(
            listOf("ING", "INg", "InG", "Ing", "iNG", "iNg", "inG", "ing", "-ING", "-INg"),
            wordEndsInIng.allStrings().take(10).toList()
        )
    }

    @Test
    fun no3Consecutive0() {
        assertEquals(
            listOf(
                "",
                "0",
                "1",
                "2",
                "00",
                "01",
                "02",
                "10",
                "11",
                "12",
                "20",
                "21",
                "22",
                "001",
                "002",
                "010",
                "011",
                "012",
                "020",
                "021"
            ),
            no3Consecutive0.allStrings().take(20).toList()
        )
    }

    @Test
    fun noConsecutiveOnes() {
        assertEquals(
            listOf(
                "",
                "0",
                "1",
                "00",
                "01",
                "10",
                "000",
                "001",
                "010",
                "100",
                "101",
                "0000",
                "0001",
                "0010",
                "0100",
                "0101",
                "1000",
                "1001",
                "1010",
                "00000"
            ),
            noConsecutiveOnes.allStrings().take(20).toList()
        )
    }

    @Test
    fun dfaHasCycleInItsDefinitionThatDoNotLeadToFinalState() {
        assertEquals(
            listOf("b"),
            dfa(setOf('a', 'b')) {
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
            }.allStrings().toList()
        )
    }

    @Test
    fun allStringsOfEmptyDFA() {
        assertEquals(
            emptyList(),
            emptyDFA(setOf('a', 'b')).allStrings().toList()
        )
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        assertEquals(
            emptyList(),
            finalStateIsNotReachableFromStart.allStrings().toList()
        )
    }

    @Test
    fun singletonEmptyString() {
        assertEquals(
            listOf(""),
            singletonEmptyString.allStrings().toList()
        )
    }

    @Test
    fun infiniteNumberOfStringsAlphabeticallySmallerThatAny() {
        assertEquals(
            listOf(
                "b",
                "ab",
                "aab",
                "aaab",
                "aaaab",
                "aaaaab",
                "aaaaaab",
                "aaaaaaab",
                "aaaaaaaab",
                "aaaaaaaaab"
            ),
            infiniteNumberOfStringsAlphabeticallySmallerThatAny.allStrings().take(10).toList()
        )
    }

    @Test
    fun anyStrings() {
        assertEquals(
            1 + 96 + 96 * 96 + 96 * 96 * 96,
            anyStringDFA((0x20.toChar()..0x7F.toChar()).toSet()).allStrings().takeWhile { it.length <= 3 }.count()
        )
    }

    @Test
    fun numbers() {
        val n = 113
        assertEquals(
            (0 until n).map { it.toString() },
            integers.allStrings().take(n).toList()
        )
    }
}