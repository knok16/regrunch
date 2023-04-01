package nfa

object Fixtures {
    val simpleChessBoard = nfa(setOf('r', 'b')) {
        val r1c1 = startState
        val r1c2 = newState()
        val r1c3 = newState()

        val r2c1 = newState()
        val r2c2 = newState()
        val r2c3 = newState()

        val r3c1 = newState()
        val r3c2 = newState()
        val r3c3 = newState()

        transition(r1c1, setOf(r1c2, r2c1), 'r')
        transition(r1c1, setOf(r2c2), 'b')

        transition(r1c2, setOf(r2c1, r2c3), 'r')
        transition(r1c2, setOf(r1c1, r2c2, r1c3), 'b')

        transition(r1c3, setOf(r1c2, r2c3), 'r')
        transition(r1c3, setOf(r2c2), 'b')

        transition(r2c1, setOf(r1c2, r3c2), 'r')
        transition(r2c1, setOf(r1c1, r2c2, r3c1), 'b')

        transition(r2c2, setOf(r1c2, r2c1, r2c3, r3c2), 'r')
        transition(r2c2, setOf(r1c1, r1c3, r3c1, r3c3), 'b')

        transition(r2c3, setOf(r1c2, r3c2), 'r')
        transition(r2c3, setOf(r1c3, r2c2, r3c3), 'b')

        transition(r3c1, setOf(r2c1, r3c2), 'r')
        transition(r3c1, setOf(r2c2), 'b')

        transition(r3c2, setOf(r2c1, r2c3), 'r')
        transition(r3c2, setOf(r3c1, r2c2, r3c3), 'b')

        transition(r3c3, setOf(r2c3, r3c2), 'r')
        transition(r3c3, setOf(r2c2), 'b')

        markAsFinal(r3c3)
    }
}