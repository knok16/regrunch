package regex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private fun symbol(vararg symbols: Char) = Symbol(symbols.toSet())
private fun union(vararg parts: RegexPart) = Union(parts.toSet())
private fun concatenation(vararg parts: RegexPart) = Concatenation(parts.toList())

class ParserTest {
    private val alphabet = (0..127).map { it.toChar() }.toSet()
    private val whitespaces = setOf(
        ' ',
        '\t',
        '\r',
        '\n',
        0x0B.toChar(), // Vertical Tab
        0x0C.toChar(), // Form Feed
        0x1C.toChar(), // File Separator
        0x1D.toChar(), // Group Separator
        0x1E.toChar(), // Record Separator
        0x1F.toChar(), // Unit Separator
    )

    @Test
    fun simpleParse() {
        assertEquals(symbol('a'), parse("""a"""))
    }

    @Test
    fun periodSpecialSymbol() {
        assertEquals(Symbol(alphabet), parse("""."""))
    }

    @Test
    fun nonPrintableCharacters() {
        assertEquals(concatenation(symbol('a'), symbol('\t'), symbol('b')), parse("""a\tb"""))
        assertEquals(concatenation(symbol('a'), symbol('\r'), symbol('b')), parse("""a\rb"""))
        assertEquals(concatenation(symbol('a'), symbol('\n'), symbol('b')), parse("""a\nb"""))
        assertEquals(concatenation(symbol('a'), symbol(0x0B.toChar()), symbol('b')), parse("""a\vb"""))
        assertEquals(concatenation(symbol('a'), symbol(0x07.toChar()), symbol('b')), parse("""a\ab"""))
        assertEquals(concatenation(symbol('a'), symbol(0x1B.toChar()), symbol('b')), parse("""a\eb"""))
        assertEquals(concatenation(symbol('a'), symbol(0x0C.toChar()), symbol('b')), parse("""a\fb"""))
    }

    @Test
    fun controlCharacters() {
        for (char in 'A'..'Z') {
            assertEquals(
                concatenation(symbol('a'), symbol((char - 'A' + 1).toChar()), symbol('b')), parse("""a\c${char}b""")
            )
        }
    }

    @Test
    fun noControlCharacter() {
        assertFailsWith<ParseException> {
            parse("""\c""")
        }.let {
            assertEquals(ParseException("No control character", 1), it)
        }
    }

    @Test
    fun unexpectedControlCharacter() {
        assertFailsWith<ParseException> {
            parse("""\c8""")
        }.let {
            assertEquals(ParseException("Unexpected control character '8' (only 'A'-'Z' allowed)", 2), it)
        }
    }

    @Test
    fun escapingSpecialCharacters() {
        assertEquals(concatenation(symbol('\\'), Symbol(alphabet)), parse("""\\."""))
        assertEquals(symbol('.'), parse("""\."""))
        assertEquals(concatenation(symbol('a'), symbol('|'), symbol('b')), parse("""a\|b"""))
        assertEquals(concatenation(symbol('('), symbol('a'), symbol(')')), parse("""\(a\)"""))
        assertEquals(concatenation(symbol('['), symbol('a'), symbol(']')), parse("""\[a]"""))
        assertEquals(concatenation(symbol('a'), symbol('*')), parse("""a\*"""))
        assertEquals(concatenation(symbol('a'), symbol('+')), parse("""a\+"""))
        assertEquals(concatenation(symbol('a'), symbol('?')), parse("""a\?"""))
        assertEquals(
            concatenation(symbol('a'), symbol('{'), symbol('1'), symbol(','), symbol('2'), symbol('}')),
            parse("""a\{1,2}""")
        )
    }

    @Test
    fun escapedAnyRegularCharacter() {
        assertEquals(concatenation(symbol('a'), symbol('7'), symbol('b')), parse("""a\7b"""))
    }

    @Test
    fun nothingToEscape() {
        assertFailsWith<ParseException> {
            parse("""abc\""")
        }.let {
            assertEquals(ParseException("No character to escape", 3), it)
        }
    }

    @Test
    fun simpleConcatenation() {
        assertEquals(concatenation(symbol('a'), symbol('b')), parse("""ab"""))
    }

    @Test
    fun simpleUnion() {
        assertEquals(union(symbol('a'), symbol('b')), parse("""a|b"""))
    }

    @Test
    fun unionNoLeftOperand() {
        assertFailsWith<ParseException> {
            parse("""|b""")
        }.let {
            assertEquals(ParseException("No left operand", 0), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(|c)""")
        }.let {
            assertEquals(ParseException("No left operand", 3), it)
        }
    }

    @Test
    fun unionNoRightOperand() {
        assertFailsWith<ParseException> {
            parse("""b|""")
        }.let {
            assertEquals(ParseException("No right operand", 1), it)
        }
        assertFailsWith<ParseException> {
            parse("""(a|)bc""")
        }.let {
            assertEquals(ParseException("No right operand", 2), it)
        }
    }

    @Test
    fun simpleKleeneStar() {
        assertEquals(Repeat(symbol('a'), 0, null), parse("""a*"""))
    }

    @Test
    fun kleeneStarNoOperand() {
        assertFailsWith<ParseException> {
            parse("""*b""")
        }.let {
            assertEquals(ParseException("No operand", 0), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(*c)""")
        }.let {
            assertEquals(ParseException("No operand", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|*)""")
        }.let {
            assertEquals(ParseException("No operand", 5), it)
        }
    }

    @Test
    fun simplePlusOperator() {
        assertEquals(Repeat(symbol('a'), 1, null), parse("""a+"""))
    }

    @Test
    fun plusOperatorNoOperand() {
        assertFailsWith<ParseException> {
            parse("""+b""")
        }.let {
            assertEquals(ParseException("No operand", 0), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(+c)""")
        }.let {
            assertEquals(ParseException("No operand", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|+)""")
        }.let {
            assertEquals(ParseException("No operand", 5), it)
        }
    }

    @Test
    fun simpleQuestionMarkOperator() {
        assertEquals(Repeat(symbol('a'), 0, 1), parse("""a?"""))
    }

    @Test
    fun questionMarkOperatorNoOperand() {
        assertFailsWith<ParseException> {
            parse("""?b""")
        }.let {
            assertEquals(ParseException("No operand", 0), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(?c)""")
        }.let {
            assertEquals(ParseException("No operand", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|?)""")
        }.let {
            assertEquals(ParseException("No operand", 5), it)
        }
    }

    @Test
    fun repeatOperator() {
        assertEquals(Repeat(symbol('a'), 2, 4), parse("""a{2,4}"""))
        assertEquals(Repeat(symbol('a'), 0, 1), parse("""a{0,1}"""))
    }

    @Test
    fun repeatOperatorExactNumberOfRepeats() {
        assertEquals(Repeat(symbol('a'), 2, 2), parse("""a{2}"""))
    }

    @Test
    fun repeatOperatorOmittedMaxLimit() {
        assertEquals(Repeat(symbol('a'), 0, null), parse("""a{0,}"""))
        assertEquals(Repeat(symbol('a'), 1, null), parse("""a{1,}"""))
        assertEquals(Repeat(symbol('a'), 2, null), parse("""a{2,}"""))
    }

    @Test
    fun severalRepeatOperators() {
        assertEquals(
            Repeat(Repeat(Repeat(Repeat(symbol('a'), 0, null), 1, null), 0, 1), 2, 2),
            parse("""a*+?{2}""")
        )
    }

    @Test
    fun repeatOperatorNoClosingCurlyBracket() {
        assertFailsWith<ParseException> {
            parse("""abc{1,2""")
        }.let {
            assertEquals(ParseException("Unbalanced curly bracket", 3), it)
        }
    }

    @Test
    fun repeatOperatorTooManyIntegers() {
        assertFailsWith<ParseException> {
            parse("""abc{1,22,3}""")
        }.let {
            assertEquals(ParseException("Unexpected number of parts in repeat operator", 3, 10), it)
        }
    }

    @Test
    fun repeatOperatorNotAnInteger() {
        assertFailsWith<ParseException> {
            parse("""abc{1,abc}""")
        }.let {
            assertEquals(ParseException("Not an integer parameter of repeat operator", 6, 8), it)
        }
    }

    @Test
    fun repeatOperatorNoOperand() {
        assertFailsWith<ParseException> {
            parse("""{2}b""")
        }.let {
            assertEquals(ParseException("No operand", 0, 2), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab({3,7}c)""")
        }.let {
            assertEquals(ParseException("No operand", 3, 7), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|{3,7})""")
        }.let {
            assertEquals(ParseException("No operand", 5, 9), it)
        }
    }

    @Test
    fun unionAndConcatenationOrder() {
        assertEquals(union(symbol('a'), concatenation(symbol('b'), symbol('c'))), parse("""a|bc"""))
        assertEquals(union(concatenation(symbol('a'), symbol('b')), symbol('c')), parse("""ab|c"""))
    }

    @Test
    fun unionAndKleeneStarOrder() {
        assertEquals(union(symbol('a'), Repeat(symbol('b'), 0, null)), parse("""a|b*"""))
    }

    @Test
    fun unionAndPlusOrder() {
        assertEquals(union(symbol('a'), Repeat(symbol('b'), 1, null)), parse("""a|b+"""))
    }

    @Test
    fun unionAndQuestionMarkOrder() {
        assertEquals(union(symbol('a'), Repeat(symbol('b'), 0, 1)), parse("""a|b?"""))
    }

    @Test
    fun unionAndRepeatOrder() {
        assertEquals(union(symbol('a'), Repeat(symbol('b'), 2, 4)), parse("""a|b{2,4}"""))
    }

    @Test
    fun concatenationAndKleeneStarOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 0, null)), parse("""ab*"""))
        assertEquals(concatenation(Repeat(symbol('a'), 0, null), symbol('b')), parse("""a*b"""))
    }

    @Test
    fun concatenationAndPlusOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 1, null)), parse("""ab+"""))
        assertEquals(concatenation(Repeat(symbol('a'), 1, null), symbol('b')), parse("""a+b"""))
    }

    @Test
    fun concatenationAndQuestionMarkOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 0, 1)), parse("""ab?"""))
        assertEquals(concatenation(Repeat(symbol('a'), 0, 1), symbol('b')), parse("""a?b"""))
    }

    @Test
    fun concatenationAndRepeatOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 2, 4)), parse("""ab{2,4}"""))
        assertEquals(concatenation(Repeat(symbol('a'), 2, 4), symbol('b')), parse("""a{2,4}b"""))
    }

    @Test
    fun bracketsTest() {
        assertEquals(
            concatenation(symbol('a'), union(symbol('b'), symbol('c')), union(symbol('d'), symbol('e'))),
            parse("a((b|c)(d|e))")
        )
    }

    @Test
    fun unbalancedLeftBracket() {
        assertFailsWith<ParseException>("Unbalanced left bracket") {
            parse("""ab(cd(e|f)g""")
        }.let {
            assertEquals(ParseException("_________________", 2), it)
        }
    }

    @Test
    fun unbalancedRightBracket() {
        assertFailsWith<ParseException> {
            parse("""ab(c|d)e)f)g""")
        }.let {
            assertEquals(ParseException("Unbalanced right bracket", 8), it)
        }
    }

    @Test
    fun setNotationSimple() {
        assertEquals(symbol('a', 'b', 'd', '0'), parse("""[abd0]"""))
    }

    @Test
    fun setNotationNoClosingBracket() {
        assertFailsWith<ParseException> {
            parse("""abc[123""")
        }.let {
            assertEquals(ParseException("Unbalanced square bracket", 3), it)
        }
    }

    @Test
    fun setNotationRanges() {
        assertEquals(symbol('3', '4', '5', '6', '7', 'b', 'c', 'd', '9'), parse("""[3-7b-d9]"""))
    }

    @Test
    fun setNotationRangesExceptions() {
        assertEquals(symbol('-', 'x'), parse("""[-x]"""))
        assertEquals(symbol('-', 'x'), parse("""[x-]"""))
        assertEquals(Symbol(alphabet - setOf('_', 'x')), parse("""[^-x]"""))
        assertEquals(Symbol(alphabet - setOf('_', 'x')), parse("""[^x-]"""))
    }

    @Test
    fun setNotationMetacharacterThatDoNotRequireEscaping() {
        assertEquals(symbol('x', '+'), parse("""[x+]"""))
        assertEquals(symbol('x', '*'), parse("""[x*]"""))
        assertEquals(symbol('x', '?'), parse("""[x?]"""))
        assertEquals(symbol('x', '.'), parse("""[x.]"""))
        assertEquals(symbol('x', ' '), parse("""[x ]"""))
        assertEquals(symbol('x', 'y', '|'), parse("""[x|y]"""))
        assertEquals(symbol('x', '(', ')'), parse("""[(x)]"""))
        assertEquals(symbol('x', '{', '}', '1', '2', ','), parse("""[x{1,2}]"""))
    }

    @Test
    fun setNotationSpecialCharacterEscaping() {
        assertEquals(symbol('a', 'b', 'c', '\\', '0', '1', '2'), parse("""[abc\\012]"""))
        assertEquals(symbol('a', 'b', 'c', ']', '0', '1', '2'), parse("""[abc\]012]"""))
        assertEquals(symbol('a', 'b', 'c', '^', '0', '1', '2'), parse("""[abc\^012]"""))
        assertEquals(symbol('a', 'b', 'c', '^', '0', '1', '2'), parse("""[\^abc012]"""))
        assertEquals(symbol('a', 'b', 'c', '-', 'e', 'f', 'g'), parse("""[abc\-efg]"""))
    }

    @Test
    fun setNotationCaretInTheMiddleDoesNotCountedAsMetacharacter() {
        assertEquals(symbol('a', 'b', 'c', '^', '0', '1', '2'), parse("""[abc^012]"""))
    }

    @Test
    fun setNotationNegation() {
        assertEquals(Symbol(alphabet - setOf('a', 'b', 'd')), parse("""[^abd]"""))
    }

    @Test
    fun shorthandCharacterClassesDigits() {
        val expected = Symbol(setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'))
        assertEquals(expected, parse("""\d"""))
        assertEquals(expected, parse("""[\d]"""))
    }

    @Test
    fun shorthandCharacterClassesWordCharacters() {
        val expected = Symbol(('A'..'Z').toSet() + ('a'..'z').toSet() + ('0'..'9').toSet() + '_')
        assertEquals(expected, parse("""\w"""))
        assertEquals(expected, parse("""[\w]"""))
    }

    @Test
    fun shorthandCharacterClassesWhitespaceCharacters() {
        val expected = Symbol(whitespaces)
        assertEquals(expected, parse("""\s"""))
        assertEquals(expected, parse("""[\s]"""))
    }

    @Test
    fun shorthandCharacterClassesNonDigits() {
        val expected = Symbol(alphabet - setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'))
        assertEquals(expected, parse("""\D"""))
        assertEquals(expected, parse("""[\D]"""))
    }

    @Test
    fun shorthandCharacterClassesNonWordCharacters() {
        val expected = alphabet - (('A'..'Z').toSet() + ('a'..'z').toSet() + ('0'..'9').toSet() + '_')
        assertEquals(Symbol(expected), parse("""\W"""))
        assertEquals(Symbol(expected), parse("""[\W]"""))
    }

    @Test
    fun shorthandCharacterClassesNonWhitespaceCharacters() {
        val expected = Symbol(alphabet - whitespaces)
        assertEquals(expected, parse("""\S"""))
        assertEquals(expected, parse("""[\S]"""))
    }

    @Test
    fun hexadecimalCharacters() {
        assertEquals(symbol('1'), parse("""\x31"""))
        assertEquals(symbol('1', '3', '5'), parse("""[\x31\x33\x35]"""))
    }

    @Test
    fun unicodeCharacters() {
        assertEquals(symbol('1'), parse("""\x0031"""))
        assertEquals(symbol('1', '3', '5'), parse("""[\x0031\x0033\x0035]"""))
        assertEquals(symbol('1', '3', '5', 'a', 'b'), parse("""[\x0031\x0033ab\x0035]"""))
    }

    @Test
    fun hexadecimalNotationEndsAbruptly() {
        assertFailsWith<ParseException> {
            parse("""abc\x""")
        }.let {
            assertEquals(ParseException("""Expected a number after \x, but get end of string""", 4), it)
        }
    }

    @Test
    fun hexadecimalNotationNotANumber() {
        assertFailsWith<ParseException> {
            parse("""abc\xNO""")
        }.let {
            assertEquals(ParseException("Not a hexadecimal number", 5), it)
        }
    }

    @Test
    fun freeSpaceMode() {
        assertEquals(concatenation(symbol('a'), symbol('b'), symbol('c')), parse("""a${System.lineSeparator()}b c"""))
        assertEquals(symbol('a', 'b', 'c', ' '), parse("""[a b c]"""))
        assertEquals(concatenation(symbol(' '), symbol('d')), parse("""\ d"""))
    }
}