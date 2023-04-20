package com.github.knok16.regrunch.epsilonnfa

import com.github.knok16.regrunch.epsilonnfa.Fixtures.example1
import kotlin.test.Test
import kotlin.test.assertEquals

class ClosureTest {
    @Test
    fun closure() {
        val a = 0
        val b = 1
        val c = 2
        val d = 3
        val e = 4
        val f = 5
        assertEquals(setOf(a), example1.closure(a))
        assertEquals(setOf(b, d), example1.closure(b))
        assertEquals(setOf(c), example1.closure(c))
        assertEquals(setOf(d), example1.closure(d))
        assertEquals(setOf(b, c, d, e), example1.closure(e))
        assertEquals(setOf(f), example1.closure(f))
    }
}