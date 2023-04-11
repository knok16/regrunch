package e2e

import State
import dfa.DFA
import dfa.Fixtures.integers
import dfa.Fixtures.numbersWithDigitSum
import dfa.allStringsAlphabetically
import dfa.languageSize
import epsilonnfa.toNFA
import nfa.toDFA
import regex.parse
import regex.toEpsilonNFA
import utils.toBigInteger
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
    fun countNumberOfStringBigNumber() {
//        val bits = "1101011101011111101001111011010110100010110011010011110110110000110100100100011100111101110000111110000000011110011010001111111010110101011011110000101110100001010101101001111101010110110011000111101000010111100011110010010101001010000000110111001111010111"
//        val pattern = bits.reversed().withIndex().filter { it.value == '1' }.map { it.index }
//            .fold(Stack<IntRange>()) { result, nextIndex ->
//                if (result.isNotEmpty() && result.peek().last + 1 == nextIndex) {
//                    result.push(result.pop().first..nextIndex)
//                } else {
//                    result.push(nextIndex..nextIndex)
//                }
//                result
//            }.joinToString(separator = "|") { range ->
//                val repeat = if (range.first == range.last) range.first.toString() else "${range.first},${range.last}"
//
//                """[01]{$repeat}${System.lineSeparator()}"""
//            }
//        val number = BigInteger(bits, 2)

        assertEquals(
            "97416270405091503282009001998959187420269453278070972930475360482792963273687".toBigInteger(),
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
                    |[01]{66,67}
                    |[01]{70,71}
                    |[01]{73,74}
                    |[01]{76}
                    |[01]{78}
                    |[01]{80,84}
                    |[01]{87}
                    |[01]{89,90}
                    |[01]{92}
                    |[01]{94}
                    |[01]{96}
                    |[01]{101}
                    |[01]{103,105}
                    |[01]{107}
                    |[01]{112,115}
                    |[01]{117,118}
                    |[01]{120}
                    |[01]{122}
                    |[01]{124,125}
                    |[01]{127}
                    |[01]{129,135}
                    |[01]{139}
                    |[01]{141,142}
                    |[01]{145,148}
                    |[01]{157,161}
                    |[01]{166,168}
                    |[01]{170,173}
                    |[01]{176,178}
                    |[01]{182}
                    |[01]{185}
                    |[01]{188}
                    |[01]{190,191}
                    |[01]{196,197}
                    |[01]{199,200}
                    |[01]{202,205}
                    |[01]{208}
                    |[01]{210,211}
                    |[01]{214,215}
                    |[01]{217}
                    |[01]{221}
                    |[01]{223,224}
                    |[01]{226}
                    |[01]{228,229}
                    |[01]{231,234}
                    |[01]{237}
                    |[01]{239,244}
                    |[01]{246}
                    |[01]{248,250}
                    |[01]{252}
                    |[01]{254,255}
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
}
