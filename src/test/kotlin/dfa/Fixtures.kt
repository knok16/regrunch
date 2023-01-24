package dfa

import State

object Fixtures {
    val taylor = dfa {
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

    val evenOnes = dfa {
        val even = startState
        val odd = newState()

        transition(even, even, '0')
        transition(even, odd, '1')
        transition(odd, odd, '0')
        transition(odd, even, '1')

        markAsFinal(even)
    }

    val oddZeros = dfa {
        val even = startState
        val odd = newState()

        transition(even, even, '1')
        transition(even, odd, '0')
        transition(odd, odd, '1')
        transition(odd, even, '0')

        markAsFinal(odd)
    }

    fun divisibilityBy(n: Int): DFA<Char, State> = dfa {
        val remainders = Array(n) { if (it == 0) startState else newState() }

        for (remainder in 0 until n) {
            for (digit in 0..9) {
                val newRemainder = (remainder * 10 + digit) % n
                transition(remainders[remainder], remainders[newRemainder], '0' + digit)
            }
        }

        markAsFinal(startState)
    }
}
