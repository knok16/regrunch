package com.github.knok16.regrunch.regex

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphabetContextCaseInsensitiveTest {
    private val alphabetContext = AlphabetContext(
        setOf(
            ' ', '\n', '\t',
            '0', '1', '2', '3',
            'a', 'b', 'c', 'd',
            'C', 'D', 'E', 'F',
            '-', '_', '+', '='
        ),
        caseInsensitive = true
    )

    @Test
    fun simple() {
        assertEquals(setOf('c', 'C'), alphabetContext.convertToChars(ExactSymbol('c')))
        assertEquals(setOf('c', 'C'), alphabetContext.convertToChars(ExactSymbol('C')))
    }

    @Test
    fun symbolIsNotLetter() {
        assertEquals(setOf(' '), alphabetContext.convertToChars(ExactSymbol(' ')))
        assertEquals(setOf('1'), alphabetContext.convertToChars(ExactSymbol('1')))
        assertEquals(setOf('-'), alphabetContext.convertToChars(ExactSymbol('-')))
        assertEquals(setOf('+'), alphabetContext.convertToChars(ExactSymbol('+')))
    }

    @Test
    fun anotherCasedLetterNotInTheAlphabet() {
        assertEquals(setOf('a'), alphabetContext.convertToChars(ExactSymbol('a')))
        assertEquals(setOf('E'), alphabetContext.convertToChars(ExactSymbol('E')))
    }

    @Test
    fun letterIsNotInAlphabetButAnotherCasedLetterIs() {
        assertEquals(setOf('a'), alphabetContext.convertToChars(ExactSymbol('A')))
        assertEquals(setOf('E'), alphabetContext.convertToChars(ExactSymbol('e')))
    }

    @Test
    fun lettersWithSingleCase() {
        val alphabet = setOf('ა', 'ბ', 'გ', 'ზ', ' ')
        val alphabetContext = AlphabetContext(alphabet, true)
        assertEquals(setOf('ა'), alphabetContext.convertToChars(ExactSymbol('ა')))
        assertEquals(setOf('ბ'), alphabetContext.convertToChars(ExactSymbol('ბ')))
        assertEquals(setOf('გ'), alphabetContext.convertToChars(ExactSymbol('გ')))
        assertEquals(setOf('ზ'), alphabetContext.convertToChars(ExactSymbol('ზ')))
    }

    @Test
    fun caseFoldingIsNotSupported() {
        // C0 Controls and Basic Latin + C1 Controls and Latin-1 Supplement
        val alphabet = (0x00..0xFF).map { it.toChar() }.toSet()
        val alphabetContext = AlphabetContext(alphabet, true)

        assertEquals(setOf('ß'), alphabetContext.convertToChars(ExactSymbol('ß')))
    }

    @Test
    fun tailoringCasingIsNotSupported() {
        // C0 Controls and Basic Latin + C1 Controls and Latin-1 Supplement
        val alphabet = (0x00..0xFF).map { it.toChar() }.toSet() + setOf('\u1E9E')
        val alphabetContext = AlphabetContext(alphabet, true)

        assertEquals(setOf('ß'), alphabetContext.convertToChars(ExactSymbol('ß')))
    }

    @Test
    fun characterWithTitleCase() {
        // Latin Extended-B
        val alphabet = (0x0180..0x024F).map { it.toChar() }.toSet()
        val alphabetContext = AlphabetContext(alphabet, true)

        val expected = setOf('\u01F1', '\u01F2', '\u01F3')
        assertEquals(expected, alphabetContext.convertToChars(ExactSymbol('\u01F1')))
        assertEquals(expected, alphabetContext.convertToChars(ExactSymbol('\u01F2')))
        assertEquals(expected, alphabetContext.convertToChars(ExactSymbol('\u01F3')))
    }
}