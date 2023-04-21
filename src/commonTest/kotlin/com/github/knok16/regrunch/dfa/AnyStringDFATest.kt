package com.github.knok16.regrunch.dfa

import kotlin.test.Test
import kotlin.test.assertEquals

class AnyStringDFATest {
    @Test
    fun simple() {
        assertEquals(
            listOf(
                "",
                "0",
                "00",
                "000",
                "0000",
                "0001",
                "001",
                "0010",
                "0011",
                "01",
                "010",
                "0100",
                "0101",
                "011",
                "0110",
                "0111",
                "1",
                "10",
                "100",
                "1000",
                "1001",
                "101",
                "1010",
                "1011",
                "11",
                "110",
                "1100",
                "1101",
                "111",
                "1110",
                "1111"
            ),
            anyStringDFA(alphabet = setOf('0', '1')).allStringsAlphabetically(4).toList()
        )
    }
}
