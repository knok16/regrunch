package epsilonnfa

object Fixtures {
    val example1 = epsilonNFA {
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
}
