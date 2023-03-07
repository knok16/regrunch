package regex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private fun symbol(vararg symbols: Char) = Symbol(symbols.toSet())
private fun union(vararg parts: RegexPart) = Union(parts.toSet())
private fun concatenation(vararg parts: RegexPart) = Concatenation(parts.toList())

class ParserTest {
    private val alphabet = (0..127).map { it.toChar() }.toSet()

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
    fun nothingToEscape() {
        assertFailsWith<ParseException>("No character to escape") {
            parse("""abc\""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(3, it.toIndex)
        }
    }

    @Test
    fun unexpectedEscapedCharacter() {
        assertFailsWith<ParseException>("Unexpected escaped character '0'") {
            parse("""abc\0def""")
        }.let {
            assertEquals(4, it.fromIndex)
            assertEquals(4, it.toIndex)
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
        assertFailsWith<ParseException>("No left operand") {
            parse("""|b""")
        }.let {
            assertEquals(0, it.fromIndex)
            assertEquals(0, it.toIndex)
        }
        assertFailsWith<ParseException>("No left operand") {
            parse("""ab(|c)""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(3, it.toIndex)
        }
    }

    @Test
    fun unionNoRightOperand() {
        assertFailsWith<ParseException>("No right operand") {
            parse("""b|""")
        }.let {
            assertEquals(1, it.fromIndex)
            assertEquals(1, it.toIndex)
        }
        assertFailsWith<ParseException>("No right operand") {
            parse("""(a|)bc""")
        }.let {
            assertEquals(2, it.fromIndex)
            assertEquals(2, it.toIndex)
        }
    }

    @Test
    fun simpleKleeneStar() {
        assertEquals(Repeat(symbol('a'), 0, null), parse("""a*"""))
    }

    @Test
    fun kleeneStarNoOperand() {
        assertFailsWith<ParseException>("No operand") {
            parse("""*b""")
        }.let {
            assertEquals(0, it.fromIndex)
            assertEquals(0, it.toIndex)
        }
        assertFailsWith<ParseException>("No operand") {
            parse("""ab(*c)""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(3, it.toIndex)
        }
    }

    @Test
    fun simplePlusOperator() {
        assertEquals(Repeat(symbol('a'), 1, null), parse("""a+"""))
    }

    @Test
    fun plusOperatorNoOperand() {
        assertFailsWith<ParseException>("No operand") {
            parse("""+b""")
        }.let {
            assertEquals(0, it.fromIndex)
            assertEquals(0, it.toIndex)
        }
        assertFailsWith<ParseException>("No operand") {
            parse("""ab(+c)""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(3, it.toIndex)
        }
    }

    @Test
    fun simpleQuestionMarkOperator() {
        assertEquals(Repeat(symbol('a'), 0, 1), parse("""a?"""))
    }

    @Test
    fun questionMarkOperatorNoOperand() {
        assertFailsWith<ParseException>("No operand") {
            parse("""?b""")
        }.let {
            assertEquals(0, it.fromIndex)
            assertEquals(0, it.toIndex)
        }
        assertFailsWith<ParseException>("No operand") {
            parse("""ab(?c)""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(3, it.toIndex)
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
    fun repeatOperatorNoClosingCurlyBracket() {
        assertFailsWith<ParseException>("Unbalanced curly bracket") {
            parse("""abc{1,2""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(3, it.toIndex)
        }
    }

    @Test
    fun repeatOperatorTooManyIntegers() {
        assertFailsWith<ParseException>("Unexpected number of parts in repeat operator") {
            parse("""abc{1,22,3}""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(10, it.toIndex)
        }
    }

    @Test
    fun repeatOperatorNotAnInteger() {
        assertFailsWith<ParseException>("Not an integer parameter of repeat operator") {
            parse("""abc{1,abc}""")
        }.let {
            assertEquals(6, it.fromIndex)
            assertEquals(8, it.toIndex)
        }
    }

    @Test
    fun repeatOperatorNoOperand() {
        assertFailsWith<ParseException>("No operand") {
            parse("""{2}b""")
        }.let {
            assertEquals(0, it.fromIndex)
            assertEquals(2, it.toIndex)
        }
        assertFailsWith<ParseException>("No operand") {
            parse("""ab({3,7}c)""")
        }.let {
            assertEquals(3, it.fromIndex)
            assertEquals(7, it.toIndex)
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
    }

    @Test
    fun concatenationAndPlusOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 1, null)), parse("""ab+"""))
    }

    @Test
    fun concatenationAndQuestionMarkOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 0, 1)), parse("""ab?"""))
    }

    @Test
    fun concatenationAndRepeatOrder() {
        assertEquals(concatenation(symbol('a'), Repeat(symbol('b'), 2, 4)), parse("""ab{2,4}"""))
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
            assertEquals(2, it.fromIndex)
            assertEquals(2, it.toIndex)
        }
    }

    @Test
    fun unbalancedRightBracket() {
        assertFailsWith<ParseException>("Unbalanced right bracket") {
            parse("""ab(c|d)e)f)g""")
        }.let {
            assertEquals(8, it.fromIndex)
            assertEquals(8, it.toIndex)
        }
    }

    @Test
    fun setNotationSimple() {
        assertEquals(symbol('a', 'b', 'd', '0'), parse("""[abd0]"""))
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
        val expected = symbol(' ', '\t', '\r', '\n', 0x0C.toChar())
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
        val expected = Symbol(alphabet - setOf(' ', '\t', '\r', '\n', 0x0C.toChar()))
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
    fun freeSpaceMode() {
        assertEquals(concatenation(symbol('a'), symbol('b'), symbol('c')), parse("""a b c"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), symbol('c'), symbol(' ')), parse("""[a b c]"""))
        assertEquals(concatenation(symbol(' '), symbol('d')), parse("""\ d"""))
    }
}