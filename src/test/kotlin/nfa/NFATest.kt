package nfa

import nfa.Fixtures.simpleChessBoard
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NFATest {
    @Test
    fun simpleChessBoard() {
        assertFalse(simpleChessBoard.accept(""))

        assertFalse(simpleChessBoard.accept("r"))
        assertFalse(simpleChessBoard.accept("b"))

        assertFalse(simpleChessBoard.accept("rr"))
        assertFalse(simpleChessBoard.accept("br"))
        assertFalse(simpleChessBoard.accept("rb"))
        assertTrue(simpleChessBoard.accept("bb"))

        assertFalse(simpleChessBoard.accept("rrr"))
        assertFalse(simpleChessBoard.accept("brr"))
        assertFalse(simpleChessBoard.accept("rbr"))
        assertFalse(simpleChessBoard.accept("bbr"))
        assertTrue(simpleChessBoard.accept("rrb"))
        assertTrue(simpleChessBoard.accept("brb"))
        assertTrue(simpleChessBoard.accept("rbb"))
        assertFalse(simpleChessBoard.accept("bbb"))

        assertFalse(simpleChessBoard.accept("rrrr"))
        assertFalse(simpleChessBoard.accept("brrr"))
        assertFalse(simpleChessBoard.accept("rbrr"))
        assertFalse(simpleChessBoard.accept("bbrr"))
        assertFalse(simpleChessBoard.accept("rrbr"))
        assertFalse(simpleChessBoard.accept("brbr"))
        assertFalse(simpleChessBoard.accept("rbbr"))
        assertFalse(simpleChessBoard.accept("bbbr"))
        assertTrue(simpleChessBoard.accept("rrrb"))
        assertTrue(simpleChessBoard.accept("brrb"))
        assertTrue(simpleChessBoard.accept("rbrb"))
        assertTrue(simpleChessBoard.accept("bbrb"))
        assertTrue(simpleChessBoard.accept("rrbb"))
        assertTrue(simpleChessBoard.accept("brbb"))
        assertTrue(simpleChessBoard.accept("rbbb"))
        assertTrue(simpleChessBoard.accept("bbbb"))

        assertFalse(simpleChessBoard.accept("rrrrr"))
        assertFalse(simpleChessBoard.accept("brrrr"))
        assertFalse(simpleChessBoard.accept("rbrrr"))
        assertFalse(simpleChessBoard.accept("bbrrr"))
        assertFalse(simpleChessBoard.accept("rrbrr"))
        assertFalse(simpleChessBoard.accept("brbrr"))
        assertFalse(simpleChessBoard.accept("rbbrr"))
        assertFalse(simpleChessBoard.accept("bbbrr"))
        assertFalse(simpleChessBoard.accept("rrrbr"))
        assertFalse(simpleChessBoard.accept("brrbr"))
        assertFalse(simpleChessBoard.accept("rbrbr"))
        assertFalse(simpleChessBoard.accept("bbrbr"))
        assertFalse(simpleChessBoard.accept("rrbbr"))
        assertFalse(simpleChessBoard.accept("brbbr"))
        assertFalse(simpleChessBoard.accept("rbbbr"))
        assertFalse(simpleChessBoard.accept("bbbbr"))
        assertTrue(simpleChessBoard.accept("rrrrb"))
        assertTrue(simpleChessBoard.accept("brrrb"))
        assertTrue(simpleChessBoard.accept("rbrrb"))
        assertTrue(simpleChessBoard.accept("bbrrb"))
        assertTrue(simpleChessBoard.accept("rrbrb"))
        assertTrue(simpleChessBoard.accept("brbrb"))
        assertTrue(simpleChessBoard.accept("rbbrb"))
        assertTrue(simpleChessBoard.accept("bbbrb"))
        assertTrue(simpleChessBoard.accept("rrrbb"))
        assertTrue(simpleChessBoard.accept("brrbb"))
        assertTrue(simpleChessBoard.accept("rbrbb"))
        assertTrue(simpleChessBoard.accept("bbrbb"))
        assertTrue(simpleChessBoard.accept("rrbbb"))
        assertTrue(simpleChessBoard.accept("brbbb"))
        assertTrue(simpleChessBoard.accept("rbbbb"))
        assertFalse(simpleChessBoard.accept("bbbbb"))
    }
}