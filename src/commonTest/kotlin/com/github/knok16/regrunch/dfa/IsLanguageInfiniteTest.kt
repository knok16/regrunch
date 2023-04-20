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
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsLanguageInfiniteTest {
    @Test
    fun taylor() {
        assertFalse(isLanguageInfinite(taylor))
    }

    @Test
    fun evenOnes() {
        assertTrue(isLanguageInfinite(evenOnes))
    }

    @Test
    fun oddZeroes() {
        assertTrue(isLanguageInfinite(oddZeroes))
    }

    @Test
    fun divisibilityBy() {
        assertTrue(isLanguageInfinite(divisibilityBy(7)))
    }

    @Test
    fun noConsecutiveOnes() {
        assertTrue(isLanguageInfinite(noConsecutiveOnes))
    }

    @Test
    fun wordEndsInIng() {
        assertTrue(isLanguageInfinite(wordEndsInIng))
    }

    @Test
    fun no3Consecutive0() {
        assertTrue(isLanguageInfinite(no3Consecutive0))
    }

    @Test
    fun no3Consecutive012() {
        assertTrue(isLanguageInfinite(no3Consecutive012))
    }

    @Test
    fun deadTentacle() {
        assertTrue(isLanguageInfinite(deadTentacle))
    }

    @Test
    fun deadTentacle2() {
        assertFalse(isLanguageInfinite(deadTentacle2))
    }

    @Test
    fun deadBlossom() {
        assertTrue(isLanguageInfinite(deadBlossom))
    }

    @Test
    fun deadBlossom2() {
        assertFalse(isLanguageInfinite(deadBlossom2))
    }

    @Test
    fun anyStringDFA() {
        assertTrue(isLanguageInfinite(anyStringDFA(setOf('a', '2'))))
    }

    @Test
    fun emptyDFA() {
        assertFalse(isLanguageInfinite(emptyDFA(setOf('a', '2'))))
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        assertFalse(isLanguageInfinite(finalStateIsNotReachableFromStart))
    }

    @Test
    fun singletonEmptyString() {
        assertFalse(isLanguageInfinite(singletonEmptyString))
    }
}