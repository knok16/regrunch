package com.github.knok16.regrunch.e2e

import com.github.knok16.regrunch.State
import com.github.knok16.regrunch.dfa.DFA
import com.github.knok16.regrunch.dfa.Fixtures.integers
import com.github.knok16.regrunch.dfa.Fixtures.numbersWithDigitSum
import com.github.knok16.regrunch.dfa.allStringsAlphabetically
import com.github.knok16.regrunch.dfa.languageSize
import com.github.knok16.regrunch.epsilonnfa.toNFA
import com.github.knok16.regrunch.nfa.toDFA
import com.github.knok16.regrunch.regex.parse
import com.github.knok16.regrunch.regex.toEpsilonNFA
import com.github.knok16.regrunch.utils.toBigInteger
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class E2ETest {
    private val asciiAlphabet = (0x20..0x7F).map { it.toChar() }.toSet()

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
        val n = 26 + 26 + 10 + 1
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

    @Test
    @Ignore
    fun possessiveQuantifiers() {
        assertEquals(
            emptyList(),
            dfa("""a++a""").allStringsAlphabetically(9).toList(),
            "Regular language defined by 'a++a' regular expression should be empty, since any 'a' will be matched by possessive quantifier, and no 'a' will be left to match last 'a' in regex"
        )
    }

    @Test
    fun longString() {
        assertEquals(
            listOf(
                "absywgwi0uhwduhwuh",
                "absywgwi1uhwduhwuh",
                "absywgwi2uhwduhwuh",
                "absywgwi3uhwduhwuh",
                "absywgwi4uhwduhwuh",
                "absywgwi5uhwduhwuh",
                "absywgwi6uhwduhwuh",
                "absywgwi7uhwduhwuh",
                "absywgwi8uhwduhwuh",
                "absywgwi9uhwduhwuh"
            ),
            dfa("""absywgwi\duhwduhwuh""").allStringsAlphabetically().toList()
        )
    }

    @Test
    fun emptySetNotation() {
        assertEquals(
            emptyList(),
            dfa("""abc[]""").allStringsAlphabetically().toList()
        )
        assertEquals(
            asciiAlphabet.map { "abc$it" },
            dfa("""abc[^]""").allStringsAlphabetically().toList()
        )
    }

    @Test
    fun countNumberOfString() {
        assertEquals(1L.toBigInteger(), languageSize(dfa("""abc""")))
        assertEquals(13L.toBigInteger(), languageSize(dfa("""a[\dabc]c""")))
        assertEquals(32L.toBigInteger(), languageSize(dfa("""[01]{5}""")))
    }

    @Test
    fun countNumberOfStringInfiniteLanguage() {
        assertEquals(null, languageSize(dfa("""a[\dabc]c+""")))
        assertEquals(null, languageSize(dfa("""a*[\dabc]c""")))
    }

    @Test
    fun numberOfIpV4() {
        assertEquals(
            (1L shl 32).toBigInteger(),
            languageSize(dfa("""((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])"""))
        )
    }

    @Test
    fun countNumberOfString2() {
        assertEquals(
            "8797657787409855447".toBigInteger(),
            languageSize(
                dfa(
                    """
                    [01]{0,2}
                    |[01]{4}
                    |[01]{6,9}
                    |[01]{12,14}
                    |[01]{16,17}
                    |[01]{25}
                    |[01]{27}
                    |[01]{30}
                    |[01]{32}
                    |[01]{34}
                    |[01]{37}
                    |[01]{40,43}
                    |[01]{47,50}
                    |[01]{52}
                    |[01]{57}
                    |[01]{59,62}
                """
                )
            )
        )
    }

    @Test
    fun integersDfa() {
        assertEquals(
            (0 until 10000).map { it.toString() }.sorted(),
            integers.allStringsAlphabetically(4).toList()
        )
    }

    @Test
    fun numberWithDigitSum() {
        val expected = (1 until 1000)
            .groupBy { (it % 10) + (it / 10 % 10) + (it / 100) }
            .mapValues { (_, numbers) ->
                numbers.map { it.toString() }.sorted()
            }

        for (sum in 1..(expected.maxOf { it.key } + 2)) {
            assertEquals(
                expected[sum] ?: emptyList(),
                numbersWithDigitSum(sum).allStringsAlphabetically(3).toList()
            )
        }
    }

    @Test
    fun emptyPattern() {
        assertEquals(
            listOf(""),
            dfa("").allStringsAlphabetically().toList()
        )
    }
}
