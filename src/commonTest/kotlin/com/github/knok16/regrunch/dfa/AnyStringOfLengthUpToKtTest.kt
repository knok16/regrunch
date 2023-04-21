package com.github.knok16.regrunch.dfa

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnyStringOfLengthUpToKtTest {

    @Test
    fun nEquals0() {
        assertEquals(
            setOf(""),
            anyStringOfLengthUpTo(alphabet = setOf('0', '1'), 0).allStringsAlphabetically().toSet()
        )
    }

    @Test
    fun nEquals1() {
        assertEquals(
            setOf("", "0", "1"),
            anyStringOfLengthUpTo(alphabet = setOf('0', '1'), 1).allStringsAlphabetically().toSet()
        )
    }

    @Test
    fun nEquals2() {
        assertEquals(
            setOf("", "0", "1", "00", "01", "10", "11"),
            anyStringOfLengthUpTo(alphabet = setOf('0', '1'), 2).allStringsAlphabetically().toSet()
        )
    }

    @Test
    fun nEquals3() {
        assertEquals(
            setOf("", "0", "1", "00", "01", "10", "11", "000", "001", "010", "011", "100", "101", "110", "111"),
            anyStringOfLengthUpTo(alphabet = setOf('0', '1'), 3).allStringsAlphabetically().toSet()
        )
    }

    @Test
    fun nEquals8() {
        val actual = anyStringOfLengthUpTo(alphabet = setOf('0', '1'), 8).allStringsAlphabetically().toSet()

        assertEquals((1 shl 9) - 1, actual.size)
        for (str in actual) {
            assertTrue(str.length <= 8)
        }
    }
}
