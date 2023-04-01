package nfa

import dfa.accept
import nfa.Fixtures.simpleChessBoard
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NFA2DFATest {
    @Test
    fun convertToDFA() {
        val dfa = simpleChessBoard.toDFA()

        assertFalse(dfa.accept(""))

        assertFalse(dfa.accept("r"))
        assertFalse(dfa.accept("b"))

        assertFalse(dfa.accept("rr"))
        assertFalse(dfa.accept("br"))
        assertFalse(dfa.accept("rb"))
        assertTrue(dfa.accept("bb"))

        assertFalse(dfa.accept("rrr"))
        assertFalse(dfa.accept("brr"))
        assertFalse(dfa.accept("rbr"))
        assertFalse(dfa.accept("bbr"))
        assertTrue(dfa.accept("rrb"))
        assertTrue(dfa.accept("brb"))
        assertTrue(dfa.accept("rbb"))
        assertFalse(dfa.accept("bbb"))

        assertFalse(dfa.accept("rrrr"))
        assertFalse(dfa.accept("brrr"))
        assertFalse(dfa.accept("rbrr"))
        assertFalse(dfa.accept("bbrr"))
        assertFalse(dfa.accept("rrbr"))
        assertFalse(dfa.accept("brbr"))
        assertFalse(dfa.accept("rbbr"))
        assertFalse(dfa.accept("bbbr"))
        assertTrue(dfa.accept("rrrb"))
        assertTrue(dfa.accept("brrb"))
        assertTrue(dfa.accept("rbrb"))
        assertTrue(dfa.accept("bbrb"))
        assertTrue(dfa.accept("rrbb"))
        assertTrue(dfa.accept("brbb"))
        assertTrue(dfa.accept("rbbb"))
        assertTrue(dfa.accept("bbbb"))

        assertFalse(dfa.accept("rrrrr"))
        assertFalse(dfa.accept("brrrr"))
        assertFalse(dfa.accept("rbrrr"))
        assertFalse(dfa.accept("bbrrr"))
        assertFalse(dfa.accept("rrbrr"))
        assertFalse(dfa.accept("brbrr"))
        assertFalse(dfa.accept("rbbrr"))
        assertFalse(dfa.accept("bbbrr"))
        assertFalse(dfa.accept("rrrbr"))
        assertFalse(dfa.accept("brrbr"))
        assertFalse(dfa.accept("rbrbr"))
        assertFalse(dfa.accept("bbrbr"))
        assertFalse(dfa.accept("rrbbr"))
        assertFalse(dfa.accept("brbbr"))
        assertFalse(dfa.accept("rbbbr"))
        assertFalse(dfa.accept("bbbbr"))
        assertTrue(dfa.accept("rrrrb"))
        assertTrue(dfa.accept("brrrb"))
        assertTrue(dfa.accept("rbrrb"))
        assertTrue(dfa.accept("bbrrb"))
        assertTrue(dfa.accept("rrbrb"))
        assertTrue(dfa.accept("brbrb"))
        assertTrue(dfa.accept("rbbrb"))
        assertTrue(dfa.accept("bbbrb"))
        assertTrue(dfa.accept("rrrbb"))
        assertTrue(dfa.accept("brrbb"))
        assertTrue(dfa.accept("rbrbb"))
        assertTrue(dfa.accept("bbrbb"))
        assertTrue(dfa.accept("rrbbb"))
        assertTrue(dfa.accept("brbbb"))
        assertTrue(dfa.accept("rbbbb"))
        assertFalse(dfa.accept("bbbbb"))
    }
}