package com.github.knok16.regrunch.regex

import kotlin.test.*

class AlphabetContextTest {
    private val alphabetContext = AlphabetContext(
        setOf(
            ' ', '\n', '\t',
            '0', '1', '2', '3',
            'a', 'b', 'c', 'd',
            'C', 'D', 'E', 'F',
            '-', '_', '+', '='
        )
    )

    @Test
    fun isWordChar() {
        assertFalse('\t'.isWordChar())
        assertFalse(' '.isWordChar())
        assertFalse('!'.isWordChar())
        assertFalse('('.isWordChar())
        assertFalse('*'.isWordChar())
        assertFalse('-'.isWordChar())
        assertFalse('.'.isWordChar())
        assertFalse('/'.isWordChar())
        assertFalse(';'.isWordChar())
        assertFalse(':'.isWordChar())
        assertFalse('<'.isWordChar())
        assertFalse('='.isWordChar())
        assertFalse('>'.isWordChar())
        assertFalse('?'.isWordChar())
        assertFalse('@'.isWordChar())
        assertFalse('['.isWordChar())
        assertFalse('\\'.isWordChar())
        assertFalse(']'.isWordChar())
        assertFalse('^'.isWordChar())
        assertFalse('`'.isWordChar())
        assertFalse('{'.isWordChar())
        assertFalse('|'.isWordChar())
        assertFalse('}'.isWordChar())
        assertFalse('~'.isWordChar())

        assertTrue('1'.isWordChar())
        assertTrue('5'.isWordChar())
        assertTrue('0'.isWordChar())
        assertTrue('A'.isWordChar())
        assertTrue('G'.isWordChar())
        assertTrue('R'.isWordChar())
        assertTrue('S'.isWordChar())
        assertTrue('Z'.isWordChar())
        assertTrue('_'.isWordChar())
        assertTrue('a'.isWordChar())
        assertTrue('f'.isWordChar())
        assertTrue('k'.isWordChar())
        assertTrue('u'.isWordChar())

        assertTrue('ა'.isWordChar())
        assertTrue('ბ'.isWordChar())
        assertTrue('გ'.isWordChar())
        assertTrue('ზ'.isWordChar())
        assertTrue('ჯ'.isWordChar())

        assertTrue('١'.isWordChar())
        assertTrue('٢'.isWordChar())
        assertTrue('٣'.isWordChar())
        assertTrue('٤'.isWordChar())
        assertTrue('٥'.isWordChar())
    }

    @Test
    fun anchorsAreNotSupported() {
        assertEquals(
            "Anchors are not supported",
            assertFailsWith<IllegalArgumentException> { alphabetContext.convertToChars(WordBoundary) }.message
        )
    }

    @Test
    fun convertExactSymbol() {
        assertEquals(setOf(' '), alphabetContext.convertToChars(ExactSymbol(' ')))
        assertEquals(setOf('a'), alphabetContext.convertToChars(ExactSymbol('a')))
        assertEquals(setOf('0'), alphabetContext.convertToChars(ExactSymbol('0')))
    }

    @Test
    fun convertExactSymbolSymbolNotInAlphabet() {
        // TODO should it throw Exception?
        assertEquals(emptySet(), alphabetContext.convertToChars(ExactSymbol('7')))
        assertEquals(emptySet(), alphabetContext.convertToChars(ExactSymbol('g')))
        assertEquals(emptySet(), alphabetContext.convertToChars(ExactSymbol('X')))
    }

    @Test
    fun setNotation() {
        assertEquals(
            setOf('a', 'b', '0', '1', '2', '3'), alphabetContext.convertToChars(
                SetNotationSymbol(
                    setOf(
                        ExactSymbol('a'),
                        ExactSymbol('b'),
                        ExactSymbol('1'),
                        DigitSymbol
                    )
                )
            )
        )
    }

    @Test
    fun setNotationWithNegation() {
        assertEquals(
            setOf(' ', '\n', '\t', 'c', 'd', 'C', 'D', 'E', 'F', '-', '_', '+', '='),
            alphabetContext.convertToChars(
                SetNotationSymbol(
                    setOf(
                        ExactSymbol('a'),
                        ExactSymbol('b'),
                        ExactSymbol('1'),
                        DigitSymbol
                    ),
                    negated = true
                )
            )
        )
    }
}