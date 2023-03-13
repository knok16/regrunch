package dfa

import State

object Fixtures {
    val taylor = dfa((0x20.toChar()..0x127.toChar()).toSet()) {
        val s = startState
        val states = IntArray(9) { newState() }

        transition(s, states[0], 'T')
        transition(states[0], states[1], 'A')
        transition(states[1], states[2], 'Y')
        transition(states[2], states[3], 'L')
        transition(states[3], states[4], 'O')
        transition(states[4], states[5], 'R')

        transition(s, states[0], 't')
        transition(states[0], states[1], 'a')
        transition(states[1], states[2], 'y')
        transition(states[2], states[3], 'l')
        transition(states[3], states[4], 'o')
        transition(states[4], states[5], 'r')

        for (c in '0'..'9') {
            transition(states[5], states[6], c)
            transition(states[6], states[7], c)
        }

        for (c in "!@#$%^&*()") {
            transition(states[7], states[8], c)
        }

        markAsFinal(states.last())
    }

    val evenOnes = dfa(setOf('0', '1')) {
        val even = startState
        val odd = newState()

        transition(even, even, '0')
        transition(even, odd, '1')
        transition(odd, odd, '0')
        transition(odd, even, '1')

        markAsFinal(even)
    }

    val oddZeroes = dfa(setOf('0', '1')) {
        val even = startState
        val odd = newState()

        transition(even, even, '1')
        transition(even, odd, '0')
        transition(odd, odd, '1')
        transition(odd, even, '0')

        markAsFinal(odd)
    }

    fun divisibilityBy(n: Int): DFA<Char, State> = dfa(('0'..'9').toSet()) {
        val remainders = Array(n) { if (it == 0) startState else newState() }

        for (remainder in 0 until n) {
            for (digit in 0..9) {
                val newRemainder = (remainder * 10 + digit) % n
                transition(remainders[remainder], remainders[newRemainder], '0' + digit)
            }
        }

        markAsFinal(startState)
    }

    val noConsecutiveOnes = dfa(setOf('0', '1')) {
        val hasNotEndedIn1 = startState
        val hasEndedIn1 = newState()
        val hadConsecutiveOnes = newState()

        transition(hasNotEndedIn1, hasNotEndedIn1, '0')
        transition(hasNotEndedIn1, hasEndedIn1, '1')

        transition(hasEndedIn1, hasNotEndedIn1, '0')
        transition(hasEndedIn1, hadConsecutiveOnes, '1')

        transition(hadConsecutiveOnes, hadConsecutiveOnes, '0')
        transition(hadConsecutiveOnes, hadConsecutiveOnes, '1')

        markAsFinal(hasNotEndedIn1)
        markAsFinal(hasEndedIn1)
    }

    val wordEndsInIng = dfa((0x20.toChar()..0x127.toChar()).toSet()) {
        val wordSymbols = ('a'..'z') + ('A'..'Z') + '-'

        val iSuffix = newState()
        val inSuffix = newState()
        val ingSuffix = newState()

        transition(startState, iSuffix, 'i')
        transition(startState, iSuffix, 'I')
        wordSymbols.filter { it !in setOf('i', 'I') }.forEach {
            transition(startState, startState, it)
        }

        transition(iSuffix, iSuffix, 'i')
        transition(iSuffix, iSuffix, 'I')
        transition(iSuffix, inSuffix, 'n')
        transition(iSuffix, inSuffix, 'N')
        wordSymbols.filter { it !in setOf('i', 'I', 'n', 'N') }.forEach {
            transition(iSuffix, startState, it)
        }

        transition(inSuffix, iSuffix, 'i')
        transition(inSuffix, iSuffix, 'I')
        transition(inSuffix, ingSuffix, 'g')
        transition(inSuffix, ingSuffix, 'G')
        wordSymbols.filter { it !in setOf('i', 'I', 'g', 'G') }.forEach {
            transition(inSuffix, startState, it)
        }

        transition(ingSuffix, iSuffix, 'i')
        transition(ingSuffix, iSuffix, 'I')
        wordSymbols.filter { it !in setOf('i', 'I') }.forEach {
            transition(ingSuffix, startState, it)
        }

        markAsFinal(ingSuffix)
    }

    val no3Consecutive0 = dfa(setOf('0', '1', '2')) {
        val endsIn0Zeroes = startState
        val endsIn1Zero = newState()
        val endsIn2Zeroes = newState()
        val had3ConsecutiveZeroes = newState()

        transition(endsIn0Zeroes, endsIn1Zero, '0')
        transition(endsIn0Zeroes, endsIn0Zeroes, '1')
        transition(endsIn0Zeroes, endsIn0Zeroes, '2')

        transition(endsIn1Zero, endsIn2Zeroes, '0')
        transition(endsIn1Zero, endsIn0Zeroes, '1')
        transition(endsIn1Zero, endsIn0Zeroes, '2')

        transition(endsIn2Zeroes, had3ConsecutiveZeroes, '0')
        transition(endsIn2Zeroes, endsIn0Zeroes, '1')
        transition(endsIn2Zeroes, endsIn0Zeroes, '2')

        transition(had3ConsecutiveZeroes, had3ConsecutiveZeroes, '0')
        transition(had3ConsecutiveZeroes, had3ConsecutiveZeroes, '1')
        transition(had3ConsecutiveZeroes, had3ConsecutiveZeroes, '2')

        markAsFinal(endsIn0Zeroes)
        markAsFinal(endsIn1Zero)
        markAsFinal(endsIn2Zeroes)
    }

    val no3Consecutive012 = dfa(setOf('0', '1', '2')) {
        val a = newState()
        val b = newState()
        val c = newState()
        val d = newState()
        val e = newState()
        val f = newState()
        val dead = newState()

        transition(startState, a, '0')
        transition(startState, b, '1')
        transition(startState, c, '2')

        transition(a, d, '0')
        transition(a, b, '1')
        transition(a, c, '2')

        transition(b, a, '0')
        transition(b, e, '1')
        transition(b, c, '2')

        transition(c, a, '0')
        transition(c, b, '1')
        transition(c, f, '2')

        transition(d, dead, '0')
        transition(d, b, '1')
        transition(d, c, '2')

        transition(e, a, '0')
        transition(e, dead, '1')
        transition(e, c, '2')

        transition(f, a, '0')
        transition(f, b, '1')
        transition(f, dead, '2')

        transition(dead, dead, '0')
        transition(dead, dead, '1')
        transition(dead, dead, '2')

        markAsFinal(startState)
        markAsFinal(a)
        markAsFinal(b)
        markAsFinal(c)
        markAsFinal(d)
        markAsFinal(e)
        markAsFinal(f)
    }

    val deadTentacle = dfa(setOf('a', 'b', 'c')) {
        val a = startState
        val b = newState()
        val c = newState()

        transition(a, b, 'a')
        transition(b, c, 'a')
        transition(c, a, 'a')

        // Dead states, connected sequentially and reachable from start state
        val d = newState()
        val e = newState()
        val f = newState()

        transition(b, d, 'b')
        transition(d, e, 'b')
        transition(e, f, 'b')

        val final = newState()
        markAsFinal(final)

        transition(c, final, 'c')
    }

    val deadBlossom = dfa(setOf('a', 'b', 'c')) {
        val a = startState
        val b = newState()
        val c = newState()

        transition(a, b, 'a')
        transition(b, c, 'a')
        transition(c, a, 'a')

        // Dead states, connected as triangle and reachable from start state
        val d = newState()
        val e = newState()
        val f = newState()
        val g = newState()

        transition(b, d, 'b')
        transition(d, e, 'b')
        transition(e, f, 'b')
        transition(f, g, 'b')
        transition(g, e, 'b')

        val final = newState()
        markAsFinal(final)

        transition(c, final, 'c')
    }

    val finalStateIsNotReachableFromStart = dfa(setOf('a', 'b', 'c')) {
        val a = startState
        val b = newState()
        val c = newState()

        transition(a, b, 'a')
        transition(b, c, 'a')
        transition(c, a, 'a')

        val d = newState()
        val e = newState()
        val f = newState()
        val g = newState()

        transition(d, e, 'b')
        transition(e, f, 'b')
        transition(f, g, 'b')
        transition(g, e, 'b')

        val final = newState()
        markAsFinal(final)

        transition(e, final, 'c')
        transition(final, a, 'c')
    }

    val singletonEmptyString = dfa(setOf('a', 'b', 'c')) {
        markAsFinal(startState)
    }
}
