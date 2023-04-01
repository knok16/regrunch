package dfa

import dfa.Fixtures.deadBlossom
import dfa.Fixtures.deadBlossom2
import dfa.Fixtures.deadTentacle
import dfa.Fixtures.deadTentacle2
import dfa.Fixtures.divisibilityBy
import dfa.Fixtures.evenOnes
import dfa.Fixtures.finalStateIsNotReachableFromStart
import dfa.Fixtures.no3Consecutive0
import dfa.Fixtures.no3Consecutive012
import dfa.Fixtures.noConsecutiveOnes
import dfa.Fixtures.oddZeroes
import dfa.Fixtures.singletonEmptyString
import dfa.Fixtures.taylor
import dfa.Fixtures.wordEndsInIng
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