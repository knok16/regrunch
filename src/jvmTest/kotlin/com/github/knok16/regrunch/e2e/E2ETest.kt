package com.github.knok16.regrunch.e2e

import com.github.knok16.regrunch.dfa.languageSize
import com.github.knok16.regrunch.epsilonnfa.toNFA
import com.github.knok16.regrunch.nfa.toDFA
import com.github.knok16.regrunch.regex.AlphabetContext
import com.github.knok16.regrunch.regex.parse
import com.github.knok16.regrunch.regex.toEpsilonNFA
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AdditionalE2ETest {
    @Test
    fun countNumberOfStringResultDoesNotFitIn64Bits() {
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
                parse(
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
                ).toEpsilonNFA(AlphabetContext(setOf('0', '1'))).toNFA().toDFA()
            )
        )
    }
}
