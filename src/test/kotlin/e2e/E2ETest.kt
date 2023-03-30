package e2e

import dfa.allStrings
import epsilonnfa.toNFA
import nfa.toDFA
import regex.parse
import regex.toEpsilonNFA
import kotlin.test.Test
import kotlin.test.assertEquals

class E2ETest {
    private val asciiAlphabet = (0..127).map { it.toChar() }.toSet()

    private fun allStringsFrom(pattern: String): Sequence<String> =
        parse(pattern).toEpsilonNFA(asciiAlphabet).toNFA().toDFA().allStrings()

    @Test
    fun smokeTest() {
        assertEquals(
            (0..999).map { it.toString() },
            allStringsFrom("""([1-9]\d{0,2})|0""").toList()
        )
    }

    @Test
    fun all4DigitPinCodes() {
        val expected = (0..9999).map { it.toString().padStart(4, padChar = '0') }
        assertEquals(expected, allStringsFrom("""[0-9]{4}""").toList())
        assertEquals(expected, allStringsFrom("""[0123456789]{4}""").toList())
        assertEquals(expected, allStringsFrom("""\d{4}""").toList())
        assertEquals(expected, allStringsFrom("""(0|1|2|3|4|5|6|7|8|9){4}""").toList())
    }

    @Test
    fun allCombinations() {
        val n = 26 + 26 + 10
        assertEquals(
            n * n * n,
            allStringsFrom("""\w{3}""").count()
        )
    }

    @Test
    fun ipAddresses() {
        assertEquals(
            listOf(
                "0.0.0.0",
                "0.0.0.1",
                "0.0.0.2",
                "0.0.0.3",
                "0.0.0.4",
                "0.0.0.5",
                "0.0.0.6",
                "0.0.0.7",
                "0.0.0.8",
                "0.0.0.9"
            ),
            allStringsFrom("""((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)""")
                .take(10)
                .toList()
        )
    }

    @Test
    fun looseIpAddressDefinition() {
        assertEquals(
            listOf(
                "0.0.0.0",
                "0.0.0.1",
                "0.0.0.2",
                "0.0.0.3",
                "0.0.0.4",
                "0.0.0.5",
                "0.0.0.6",
                "0.0.0.7",
                "0.0.0.8",
                "0.0.0.9"
            ),
            allStringsFrom("""(\d{1,3}\.){3}(\d{1,3})""")
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
                "b",
                "aa",
                "ab",
                "bb",
                "aaa",
                "aab",
                "abb",
                "bbb",
                "aaaa",
                "aaab",
                "aabb",
                "abbb",
                "bbbb",
                "aaaaa",
                "aaaab",
                "aaabb",
                "aabbb",
                "abbbb",
                "bbbbb",
                "aaaaaa",
                "aaaaab",
                "aaaabb",
                "aaabbb",
                "aabbbb",
                "abbbbb",
                "bbbbbb"
            ),
            allStringsFrom("""a*b*""")
                .takeWhile { it.length <= 6 }
                .toList()
        )
    }
}