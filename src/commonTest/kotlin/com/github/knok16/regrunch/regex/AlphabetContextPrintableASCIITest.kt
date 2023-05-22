package com.github.knok16.regrunch.regex

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphabetContextPrintableASCIITest {
    @Test
    fun getAnySymbol() {
        assertEquals(
            setOf(
                ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~',
            ),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(AnySymbol)
        )
    }

    @Test
    fun getDigits() {
        assertEquals(
            setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(DigitSymbol)
        )
    }

    @Test
    fun getNonDigits() {
        assertEquals(
            setOf(
                ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~'
            ),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(NonDigitSymbol)
        )
    }

    @Test
    fun getWhitespaces() {
        assertEquals(
            setOf(' '),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(WhitespaceSymbol)
        )
    }

    @Test
    fun getNonWhitespaces() {
        assertEquals(
            setOf(
                '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~',
            ),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(NonWhitespaceSymbol)
        )
    }

    @Test
    fun getVerticalWhitespaces() {
        assertEquals(
            emptySet(),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(VerticalWhitespaceSymbol)
        )
    }

    @Test
    fun getHorizontalWhitespaces() {
        assertEquals(
            setOf(' '),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(HorizontalWhitespaceSymbol)
        )
    }

    @Test
    fun getWordCharacters() {
        assertEquals(
            setOf(
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z',
                '_',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z'
            ),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(WordSymbol)
        )
    }

    @Test
    fun getNonWordCharacters() {
        assertEquals(
            setOf(
                ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')',
                '*', '+', ',', '-', '.', '/', ':', ';', '<', '=',
                '>', '?', '@', '[', '\\', ']', '^', '`', '{', '|',
                '}', '~'
            ),
            AlphabetContext.PRINTABLE_ASCII.convertToChars(NonWordSymbol)
        )
    }

    @Test
    fun getAlphabet() {
        assertEquals(
            setOf(
                ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~'
            ),
            AlphabetContext.PRINTABLE_ASCII.alphabet
        )
    }
}