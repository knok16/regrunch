package com.github.knok16.regrunch.epsilonnfa

object Fixtures {
    val example1 = epsilonNFA(setOf('0', '1')) {
        val a = startState
        val b = newState()
        val c = newState()
        val d = newState()
        val e = newState()
        val f = newState()

        transition(a, setOf(b), '1')
        transition(a, setOf(e), '0')

        transition(b, setOf(c), '1')
        epsilonTransition(b, d)

        transition(c, setOf(d), '1')

        transition(e, setOf(f), '0')
        epsilonTransition(e, b)
        epsilonTransition(e, c)

        transition(f, setOf(d), '0')

        markAsFinal(d)
    }

    // (ab)*∪(aba)*
    val example2 = epsilonNFA(setOf('a', 'b')) {
        val q1 = newState()
        val q2 = newState()
        val q3 = newState()
        val q4 = newState()
        val q5 = newState()

        transition(q1, setOf(q3), 'a')
        transition(q3, setOf(q1), 'b')

        transition(q2, setOf(q4), 'a')
        transition(q4, setOf(q5), 'b')
        transition(q5, setOf(q2), 'a')

        epsilonTransition(startState, q1)
        epsilonTransition(startState, q2)

        markAsFinal(q1)
        markAsFinal(q2)
    }
}
