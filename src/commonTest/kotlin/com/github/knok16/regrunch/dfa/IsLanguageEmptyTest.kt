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

class IsLanguageEmptyTest {
    @Test
    fun taylor() {
        assertFalse(isLanguageEmpty(taylor))
    }

    @Test
    fun evenOnes() {
        assertFalse(isLanguageEmpty(evenOnes))
    }

    @Test
    fun oddZeroes() {
        assertFalse(isLanguageEmpty(oddZeroes))
    }

    @Test
    fun divisibilityBy() {
        assertFalse(isLanguageEmpty(divisibilityBy(7)))
    }

    @Test
    fun noConsecutiveOnes() {
        assertFalse(isLanguageEmpty(noConsecutiveOnes))
    }

    @Test
    fun wordEndsInIng() {
        assertFalse(isLanguageEmpty(wordEndsInIng))
    }

    @Test
    fun no3Consecutive0() {
        assertFalse(isLanguageEmpty(no3Consecutive0))
    }

    @Test
    fun no3Consecutive012() {
        assertFalse(isLanguageEmpty(no3Consecutive012))
    }

    @Test
    fun deadTentacle() {
        assertFalse(isLanguageEmpty(deadTentacle))
    }

    @Test
    fun deadTentacle2() {
        assertFalse(isLanguageEmpty(deadTentacle2))
    }

    @Test
    fun deadBlossom() {
        assertFalse(isLanguageEmpty(deadBlossom))
    }

    @Test
    fun deadBlossom2() {
        assertFalse(isLanguageEmpty(deadBlossom2))
    }

    @Test
    fun anyStringDFA() {
        assertFalse(isLanguageEmpty(anyStringDFA(setOf('a', '2'))))
    }

    @Test
    fun emptyDFA() {
        assertTrue(isLanguageEmpty(emptyDFA(setOf('a', '2'))))
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        assertTrue(isLanguageEmpty(finalStateIsNotReachableFromStart))
    }

    @Test
    fun singletonEmptyString() {
        assertFalse(isLanguageEmpty(singletonEmptyString))
    }
}
