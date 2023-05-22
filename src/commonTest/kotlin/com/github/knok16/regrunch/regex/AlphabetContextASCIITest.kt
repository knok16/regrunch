package com.github.knok16.regrunch.regex

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphabetContextASCIITest {
    @Test
    fun getAnySymbol() {
        assertEquals(
            setOf(
                '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\t',
                '\u000b', '\u000c', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013',
                '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d',
                '\u001e', '\u001f', ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~', '\u007f'
            ),
            AlphabetContext.ASCII.convertToChars(AnySymbol)
        )
    }

    @Test
    fun getDigits() {
        assertEquals(
            setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'),
            AlphabetContext.ASCII.convertToChars(DigitSymbol)
        )
    }

    @Test
    fun getNonDigits() {
        assertEquals(
            setOf(
                '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\t',
                '\n', '\u000b', '\u000c', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013',
                '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d',
                '\u001e', '\u001f', ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~', '\u007f'
            ),
            AlphabetContext.ASCII.convertToChars(NonDigitSymbol)
        )
    }

    @Test
    fun getWhitespaces() {
        assertEquals(
            setOf(
                '\t',
                '\n',           // Line Feed
                0x0B.toChar(),  // Vertical Tab
                0x0C.toChar(),  // Form Feed
                '\r',           // Carriage Return
                0x1C.toChar(),
                0x1D.toChar(),
                0x1E.toChar(),
                0x1F.toChar(),
                ' '
            ),
            AlphabetContext.ASCII.convertToChars(WhitespaceSymbol)
        )
    }

    @Test
    fun getNonWhitespaces() {
        assertEquals(
            setOf(
                '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\u000e',
                '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018',
                '\u0019', '\u001a', '\u001b', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~', '\u007f'
            ),
            AlphabetContext.ASCII.convertToChars(NonWhitespaceSymbol)
        )
    }

    @Test
    fun getVerticalWhitespaces() {
        assertEquals(
            setOf(
                '\n',           // Line Feed
                0x0B.toChar(),  // Vertical Tab
                0x0C.toChar(),  // Form Feed
                '\r'            // Carriage Return
            ),
            AlphabetContext.ASCII.convertToChars(VerticalWhitespaceSymbol)
        )
    }

    @Test
    fun getHorizontalWhitespaces() {
        assertEquals(
            setOf('\t', ' '),
            AlphabetContext.ASCII.convertToChars(HorizontalWhitespaceSymbol)
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
            AlphabetContext.ASCII.convertToChars(WordSymbol)
        )
    }

    @Test
    fun getNonWordCharacters() {
        assertEquals(
            setOf(
                '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\t',
                '\n', '\u000b', '\u000c', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013',
                '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d',
                '\u001e', '\u001f', ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', ':', ';',
                '<', '=', '>', '?', '@', '[', '\\', ']', '^', '`',
                '{', '|', '}', '~', '\u007f'
            ),
            AlphabetContext.ASCII.convertToChars(NonWordSymbol)
        )
    }

    @Test
    fun getAlphabet() {
        assertEquals(
            setOf(
                '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\t',
                '\n', '\u000b', '\u000c', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013',
                '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d',
                '\u001e', '\u001f', ' ', '!', '"', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
                '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '{', '|', '}', '~', '\u007f'
            ),
            AlphabetContext.ASCII.alphabet
        )
    }
}