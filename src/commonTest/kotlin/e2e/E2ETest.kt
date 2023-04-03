package e2e

import State
import dfa.DFA
import dfa.allStringsAlphabetically
import epsilonnfa.toNFA
import nfa.toDFA
import regex.parse
import regex.toEpsilonNFA
import kotlin.test.Test
import kotlin.test.assertEquals

class E2ETest {
    private val asciiAlphabet = (0..127).map { it.toChar() }.toSet()

    private fun dfa(pattern: String): DFA<Char, State> =
        parse(pattern).toEpsilonNFA(asciiAlphabet).toNFA().toDFA()

    @Test
    fun smokeTest() {
        assertEquals(
            (0..999).map { it.toString() }.sorted().toList(),
            dfa("""([1-9]\d{0,2})|0""").allStringsAlphabetically().toList()
        )
    }

    @Test
    fun all4DigitPinCodes() {
        val expected = (0..9999).map { it.toString().padStart(4, padChar = '0') }
        assertEquals(expected, dfa("""[0-9]{4}""").allStringsAlphabetically().toList())
        assertEquals(expected, dfa("""[0123456789]{4}""").allStringsAlphabetically().toList())
        assertEquals(expected, dfa("""\d{4}""").allStringsAlphabetically().toList())
        assertEquals(expected, dfa("""(0|1|2|3|4|5|6|7|8|9){4}""").allStringsAlphabetically().toList())
    }

    @Test
    fun allCombinations() {
        val n = 26 + 26 + 10
        assertEquals(
            n * n * n,
            dfa("""\w{3}""").allStringsAlphabetically().count()
        )
    }

    @Test
    fun ipAddresses() {
        assertEquals(
            listOf(
                "0.0.0.0",
                "0.0.0.00",
                "0.0.0.000",
                "0.0.0.001",
                "0.0.0.002",
                "0.0.0.003",
                "0.0.0.004",
                "0.0.0.005",
                "0.0.0.006",
                "0.0.0.007"
            ),
            dfa("""((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)""")
                .allStringsAlphabetically()
                .take(10)
                .toList()
        )
    }

    @Test
    fun looseIpAddressDefinition() {
        assertEquals(
            listOf(
                "0.0.0.0",
                "0.0.0.00",
                "0.0.0.000",
                "0.0.0.001",
                "0.0.0.002",
                "0.0.0.003",
                "0.0.0.004",
                "0.0.0.005",
                "0.0.0.006",
                "0.0.0.007"
            ),
            dfa("""(\d{1,3}\.){3}(\d{1,3})""")
                .allStringsAlphabetically()
                .take(10)
                .toList()
        )
    }

    @Test
    fun noLeakingOfKleeneStar() {
        assertEquals(
            listOf(
                "",
                "a",
                "aa",
                "aaa",
                "aaaa",
                "aaaaa",
                "aaaaaa",
                "aaaaab",
                "aaaab",
                "aaaabb",
                "aaab",
                "aaabb",
                "aaabbb",
                "aab",
                "aabb",
                "aabbb",
                "aabbbb",
                "ab",
                "abb",
                "abbb",
                "abbbb",
                "abbbbb",
                "b",
                "bb",
                "bbb",
                "bbbb",
                "bbbbb",
                "bbbbbb"
            ),
            dfa("""a*b*""").allStringsAlphabetically(6).toList()
        )
    }

    @Test
    fun stringGenerationDoesNotGenerateStringInAdvance() {
        assertEquals(
            "blablabla",
            dfa("""[a-z]{10,}|blablabla""").allStringsAlphabetically(9).first()
        )
    }
}