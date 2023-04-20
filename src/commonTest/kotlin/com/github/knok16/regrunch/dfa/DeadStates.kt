package com.github.knok16.regrunch.dfa

import com.github.knok16.regrunch.dfa.Fixtures.deadBlossom
import com.github.knok16.regrunch.dfa.Fixtures.deadTentacle
import com.github.knok16.regrunch.dfa.Fixtures.finalStateIsNotReachableFromStart
import kotlin.test.Test
import kotlin.test.assertEquals

class DeadStates {
    @Test
    fun deadTentacle() {
        assertEquals(setOf(3, 4, 5, 7), deadTentacle.deadStates())
    }

    @Test
    fun deadBlossom() {
        assertEquals(setOf(3, 4, 5, 6, 8), deadBlossom.deadStates())
    }

    @Test
    fun finalStateIsNotReachableFromStart() {
        assertEquals(setOf(0, 1, 2, 8), finalStateIsNotReachableFromStart.deadStates())
    }
}
