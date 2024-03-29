package com.github.knok16.regrunch.dfa

import com.github.knok16.regrunch.dfa.Fixtures.deadBlossom
import com.github.knok16.regrunch.dfa.Fixtures.deadBlossom2
import com.github.knok16.regrunch.dfa.Fixtures.deadTentacle
import com.github.knok16.regrunch.dfa.Fixtures.deadTentacle2
import com.github.knok16.regrunch.dfa.Fixtures.divisibilityBy
import com.github.knok16.regrunch.dfa.Fixtures.evenOnes
import com.github.knok16.regrunch.dfa.Fixtures.finalStateIsNotReachableFromStart
import com.github.knok16.regrunch.dfa.Fixtures.no3Consecutive0
import com.github.knok16.regrunch.dfa.Fixtures.no3Consecutive012
import com.github.knok16.regrunch.dfa.Fixtures.noConsecutiveOnes
import com.github.knok16.regrunch.dfa.Fixtures.oddZeroes
import com.github.knok16.regrunch.dfa.Fixtures.singletonEmptyString
import com.github.knok16.regrunch.dfa.Fixtures.taylor
import com.github.knok16.regrunch.dfa.Fixtures.wordEndsInIng
import com.github.knok16.regrunch.utils.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LanguageSizeTest {
    @Test
    fun simple() {
        assertEquals(1L.toBigInteger(), languageSize(dfa(setOf('0', '1')) {
            val finalState = newState()

            transition(startState, finalState, '0')

            markAsFinal(finalState)
        }))
    }

    @Test
    fun taylor() {
        assertEquals((2L * 2 * 2 * 2 * 2 * 2 * 10 * 10 * 10).toBigInteger(), languageSize(taylor))
    }

    @Test
    fun evenOnes() {
        assertNull(languageSize(evenOnes))
    }

    @Test
    fun oddZeroes() {
        assertNull(languageSize(oddZeroes))
    }

    @Test
    fun divisibilityBy() {
        assertNull(languageSize(divisibilityBy(7)))
    }

    @Test
    fun noConsecutiveOnes() {
        assertNull(languageSize(noConsecutiveOnes))
    }

    @Test
    fun wordEndsInIng() {
        assertNull(languageSize(wordEndsInIng))
    }

    @Test
    fun no3Consecutive0() {
        assertNull(languageSize(no3Consecutive0))
    }

    @Test
    fun no3Consecutive012() {
        assertNull(languageSize(no3Consecutive012))
    }

    @Test
    fun deadTentacle() {
        assertNull(languageSize(deadTentacle))
    }

    @Test
    fun deadTentacle2() {
        assertEquals(2L.toBigInteger(), languageSize(deadTentacle2))
    }

    @Test
    fun deadBlossom() {
        assertNull(languageSize(deadBlossom))
    }

    @Test
    fun deadBlossom2() {
        assertEquals(2L.toBigInteger(), languageSize(deadBlossom2))
    }

    @Test
    fun anyStringDFA() {
        assertNull(languageSize(anyStringDFA(setOf('a', '2'))))
    }

    @Test
    fun emptyDFA() {
        assertEquals(0L.toBigInteger(), languageSize(emptyDFA(setOf('a', '2'))))
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        assertEquals(0L.toBigInteger(), languageSize(finalStateIsNotReachableFromStart))
    }

    @Test
    fun singletonEmptyString() {
        assertEquals(1L.toBigInteger(), languageSize(singletonEmptyString))
    }
}
