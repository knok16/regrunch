package regex

import dfa.allStringsAlphabetically
import epsilonnfa.toNFA
import nfa.toDFA
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private fun symbol(symbol: Char) = ExactSymbol(symbol)
private fun symbols(vararg symbols: Char) = symbols.map { ExactSymbol(it) }.toSet()
private fun concatenation(str: String) = Concatenation(str.map { ExactSymbol(it) })
private fun concatenation(vararg parts: RegexPart) = Concatenation(parts.toList())
private fun union(vararg parts: RegexPart) = Union(parts.toSet())
private fun anySymbol(vararg symbols: Char) = SetNotationSymbol(symbols.map { ExactSymbol(it) }.toSet())

// TODO rethink indexes in errors when string ends abruptly
class ParserTest {
    @Test
    fun simpleParse() {
        assertEquals(symbol('a'), parse("""a"""))
    }

    @Test
    fun emptyStringAsPattern() {
        assertEquals(concatenation(), parse(""""""))
    }

    @Test
    fun periodSpecialSymbol() {
        assertEquals(AnySymbol, parse("""."""))
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
    fun nonPrintableCharactersInSetNotation() {
        assertEquals(SetNotationSymbol(symbols('a', '\t', 'b')), parse("""[a\tb]"""))
        assertEquals(SetNotationSymbol(symbols('a', '\r', 'b')), parse("""[a\rb]"""))
        assertEquals(SetNotationSymbol(symbols('a', '\n', 'b')), parse("""[a\nb]"""))
        assertEquals(SetNotationSymbol(symbols('a', 0x0B.toChar(), 'b')), parse("""[a\vb]"""))
        assertEquals(SetNotationSymbol(symbols('a', 0x07.toChar(), 'b')), parse("""[a\ab]"""))
        assertEquals(SetNotationSymbol(symbols('a', 0x10.toChar(), 'b')), parse("""[a\bb]"""))
        assertEquals(SetNotationSymbol(symbols('a', 0x1B.toChar(), 'b')), parse("""[a\eb]"""))
        assertEquals(SetNotationSymbol(symbols('a', 0x0C.toChar(), 'b')), parse("""[a\fb]"""))
    }

    @Test
    fun controlCharacters() {
        for (char in 'A'..'Z') {
            assertEquals(
                concatenation(symbol('a'), symbol((char - 'A' + 1).toChar()), symbol('b')), parse("""a\c${char}b""")
            )
        }
        for (char in 'a'..'z') {
            assertEquals(
                concatenation(symbol('a'), symbol((char - 'a' + 1).toChar()), symbol('b')), parse("""a\c${char}b""")
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
            assertEquals(ParseException("Unexpected control character '8' (only 'A'-'Z' or 'a'-'z' allowed)", 2), it)
        }
    }

    @Test
    fun escapingSpecialCharacters() {
        assertEquals(concatenation(symbol('\\'), AnySymbol), parse("""\\."""))
        assertEquals(symbol('.'), parse("""\."""))
        assertEquals(symbol('^'), parse("""\^"""))
        assertEquals(symbol('$'), parse("""\$"""))
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
    fun unionEmptyLeftOperand() {
        assertEquals(union(concatenation(), symbol('b')), parse("""|b"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), union(concatenation(), symbol('c'))), parse("""ab(|c)"""))
    }

    @Test
    fun unionEmptyRightOperand() {
        assertEquals(union(symbol('b'), concatenation()), parse("""b|"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), union(symbol('c'), concatenation())), parse("""ab(c|)"""))
    }

    @Test
    fun unionEmptyBothOperands() {
        assertEquals(concatenation(), parse("""|"""))
        assertEquals(concatenation(), parse("""(|)"""))
        assertEquals(concatenation(), parse("""||||||"""))
        assertEquals(concatenation(symbol('a'), symbol('b')), parse("""ab(|)"""))
    }

    @Test
    fun simpleKleeneStar() {
        assertEquals(Repeat(symbol('a'), 0, null), parse("""a*"""))
    }

    @Test
    fun kleeneStarNoOperand() {
        assertException(
            setOf("(|*b"),
            ParseException("No operand for repeat operator", 2)
        )
        assertException(
            setOf("*b", "*", "*(|*", "*(|a", "*))a", "*))|", "*a(*", "*a*a", "*|", "*|*(", "*|**", "*||a", "*|||"),
            ParseException("No operand for repeat operator", 0)
        )
        assertException(
            setOf("(*", "(*(", "(*((", "(*()", "(**", "(**(", "(*a|", "(*|", "(*|(", "(*|)", "(*|*", "(*|a", "(*||"),
            ParseException("No operand for repeat operator", 1)
        )
        assertException(
            setOf("((*", "((*(", "((*)", "((**", "((*a", "((*|", "a(*", "a(*(", "a(*)", "a(**", "a(*a", "a(*|"),
            ParseException("No operand for repeat operator", 2)
        )
        assertException(
            setOf("ab(*c)", "(((*", "(a(*", "a((*", "a*(*", "aa(*", "a|(*"),
            ParseException("No operand for repeat operator", 3)
        )
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
            assertEquals(ParseException("No operand for repeat operator", 0), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(+c)""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|+)""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 5), it)
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
            assertEquals(ParseException("No operand for repeat operator", 0), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(?c)""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|?)""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 5), it)
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
            parse("""(((a*)+)?){2}""")
        )
    }

    @Test
    fun quantifierAfterQuantifier() {
        assertEquals(
            ParseException("No operand for repeat operator", 2),
            assertFailsWith<ParseException> { parse("""a**""") }
        )
        assertEquals(
            ParseException("No operand for repeat operator", 3),
            assertFailsWith<ParseException> { parse("""a*+?{2}""") }
        )
        assertEquals(
            ParseException("No operand for repeat operator", 4, 6),
            assertFailsWith<ParseException> { parse("""a{2}{3}""") }
        )
        assertEquals(
            ParseException("No operand for repeat operator", 2),
            assertFailsWith<ParseException> { parse("""a+*""") }
        )
    }

    @Test
    fun repeatOperatorNoClosingCurlyBracket() {
        assertFailsWith<ParseException> {
            parse("""abc{12""")
        }.let {
            assertEquals(ParseException("Unbalanced curly bracket", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{12,""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got end of input", 7), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{12,34""")
        }.let {
            assertEquals(ParseException("Unbalanced curly bracket", 3), it)
        }
    }

    @Test
    fun repeatOperatorTooManyIntegers() {
        assertFailsWith<ParseException> {
            parse("""abc{1,2,3}""")
        }.let {
            assertEquals(ParseException("Expected '}' but got ','", 7, 7), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{1,22,3}""")
        }.let {
            assertEquals(ParseException("Expected '}' but got ','", 8, 8), it)
        }
    }

    @Test
    fun repeatOperatorFirstIntegerIsRequired() {
        assertFailsWith<ParseException> {
            parse("""abc{}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got '}'", 4), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{,}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got ','", 4), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{,2}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got ','", 4), it)
        }
    }

    @Test
    fun repeatOperatorNotAnInteger() {
        assertFailsWith<ParseException> {
            parse("""abc{abc,1}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got 'a'", 4), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{1,abc}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got 'a'", 6), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{-1,}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got '-'", 4), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{1,-1}""")
        }.let {
            assertEquals(ParseException("Expected decimal digit but got '-'", 6), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc{1,2 3,4}""")
        }.let {
            assertEquals(ParseException("Expected '}' but got '3'", 8), it)
        }
    }

    @Test
    fun repeatOperator0Repeats() {
        assertEquals(Repeat(symbol('a'), 0, 0), parse("""a{0}"""))
        assertEquals(Repeat(symbol('a'), 0, 0), parse("""a{0,0}"""))
    }

    @Test
    fun repeatOperatorRangeValidation() {
        assertFailsWith<ParseException> {
            parse("""abc{3,2}""")
        }.let {
            assertEquals(
                ParseException(
                    "Min number of repeats (3) should be less or equals than max (2) number of repeats",
                    3,
                    7
                ), it
            )
        }
    }

    @Test
    fun repeatOperatorNoOperand() {
        assertFailsWith<ParseException> {
            parse("""{2}b""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 0, 2), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab({3,7}c)""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 3, 7), it)
        }
        assertFailsWith<ParseException> {
            parse("""ab(c|{3,7})""")
        }.let {
            assertEquals(ParseException("No operand for repeat operator", 5, 9), it)
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
        assertException(
            setOf("(", "((a)", "(a", "(a*", "(a*a", "(aa", "(aa*", "(aaa", "(a|a"),
            ParseException("Unbalanced left bracket", 0)
        )
        assertException(
            setOf("((", "a(", "((a", "((a*", "((aa", "a(a", "a(a*", "a(aa"),
            ParseException("Unbalanced left bracket", 1)
        )
        assertException(
            setOf("ab(cd(e|f)g", "(((a", "(a(a", "a((a", "a*(a", "aa(a", "a|(a", "(((", "a((", "a*(", "a|("),
            ParseException("Unbalanced left bracket", 2)
        )
        assertException(
            setOf("(a|(", "a(((", "a(a(", "aa((", "aa*(", "aaa(", "aa|("),
            ParseException("Unbalanced left bracket", 3)
        )
    }

    @Test
    fun unbalancedRightBracket() {
        assertException(
            setOf(")", ")(", ")((", ")(((", ")))a", "))|", ")*)(", ")*a*", ")a(|", ")aa", ")|()", ")|*a", ")|||"),
            ParseException("Unbalanced right bracket", 0)
        )
        assertException(
            setOf("a)", "a)(", "a)((", "a)()", "a))a", "a)a)", "a)a*", "a)aa", "a)a|", "a)|", "a)|*", "a)|a", "a)||"),
            ParseException("Unbalanced right bracket", 1)
        )
        assertException(
            setOf("a*)", "a*)(", "a*))", "a*)*", "a*)a", "a*)|", "aa)", "aa)(", "aa))", "aa)*", "aa)a", "aa)|"),
            ParseException("Unbalanced right bracket", 2)
        )
        assertException(
            setOf("(a))", "a*a)", "aa*)", "aaa)", "a|a)"),
            ParseException("Unbalanced right bracket", 3)
        )
        assertException(
            setOf("ab(c|d)e)f)g"),
            ParseException("Unbalanced right bracket", 8)
        )
    }

    @Test
    fun emptyBrackets() {
        assertEquals(concatenation("abcdef"), parse("""abc()def"""))
        assertEquals(concatenation(), parse("""()"""))
        assertEquals(concatenation(), parse("""(((()())()(()())))"""))
        assertEquals(concatenation(), parse("""(( ))"""))
        assertEquals(concatenation(), parse("""(( ))||((|))"""))
        assertEquals(union(symbol('a'), concatenation()), parse("""a|(())"""))
    }

    @Test
    fun setNotationSimple() {
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'd', '0')), parse("""[abd0]"""))
    }

    @Test
    fun emptySetNotation() {
        assertEquals(SetNotationSymbol(emptySet()), parse("""[]"""))
        assertEquals(SetNotationSymbol(emptySet(), negated = true), parse("""[^]"""))
    }

    @Test
    fun setNotationNoClosingBracket() {
        assertFailsWith<ParseException> {
            parse("""abc[123""")
        }.let {
            assertEquals(ParseException("Unbalanced square bracket", 3), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc[123-""")
        }.let {
            assertEquals(ParseException("Unbalanced square bracket", 3), it)
        }
    }

    @Test
    fun setNotationRanges() {
        assertEquals(SetNotationSymbol(symbols('3', '4', '5', '6', '7', 'b', 'c', 'd', '9')), parse("""[3-7b-d9]"""))
        assertEquals(SetNotationSymbol(symbols('a', ' ', 'b')), parse("""[a\x20-\x20b]"""))
        assertEquals(SetNotationSymbol(symbols('ბ', 'გ', 'დ')), parse("""[\u10D1-\u10D3]"""))

        assertEquals(SetNotationSymbol(symbols('2', '3', '4')), parse("""[\x32-4]"""))
        assertEquals(SetNotationSymbol(symbols('3', '4', '5')), parse("""[3-\x35]"""))

        assertEquals(SetNotationSymbol(symbols('4', '5', '6')), parse("""[\u0034-6]"""))
        assertEquals(SetNotationSymbol(symbols('5', '6', '7')), parse("""[5-\u0037]"""))

        assertEquals(SetNotationSymbol(symbols('-', '.', '/', '0')), parse("""[\u002D-\x30]"""))
        assertEquals(SetNotationSymbol(symbols('-', '.', '/', '0')), parse("""[\x2D-\u0030]"""))

        assertEquals(SetNotationSymbol(symbols(0x07.toChar(), 0x08.toChar(), '\t')), parse("""[\a-\t]"""))
        assertEquals(SetNotationSymbol(symbols(0x07.toChar(), 0x08.toChar(), '\t')), parse("""[\cG-\ci]"""))
    }

    @Test
    fun setNotationRangesExceptions() {
        assertEquals(SetNotationSymbol(symbols('-', 'x')), parse("""[-x]"""))
        assertEquals(SetNotationSymbol(symbols('x', '-')), parse("""[x-]"""))

        assertEquals(SetNotationSymbol(symbols('-', '\t')), parse("""[-\t]"""))
        assertEquals(SetNotationSymbol(symbols('\t', '-')), parse("""[\t-]"""))

        assertEquals(SetNotationSymbol(setOf(symbol('-'), DigitSymbol)), parse("""[-\d]"""))
        assertEquals(SetNotationSymbol(setOf(DigitSymbol, symbol('-'))), parse("""[\d-]"""))

        assertEquals(SetNotationSymbol(symbols('-', 'x'), negated = true), parse("""[^-x]"""))
        assertEquals(SetNotationSymbol(symbols('x', '-'), negated = true), parse("""[^x-]"""))

        assertEquals(SetNotationSymbol(symbols('-', '\t'), negated = true), parse("""[^-\t]"""))
        assertEquals(SetNotationSymbol(symbols('\t', '-'), negated = true), parse("""[^\t-]"""))

        assertEquals(SetNotationSymbol(setOf(symbol('-'), DigitSymbol), negated = true), parse("""[^-\d]"""))
        assertEquals(SetNotationSymbol(setOf(DigitSymbol, symbol('-')), negated = true), parse("""[^\d-]"""))

        // TODO decide should parser tolerate this or throw exception
        assertEquals(SetNotationSymbol(setOf(DigitSymbol, symbol('-'), symbol('7'))), parse("""[\d-7]"""))
        assertEquals(SetNotationSymbol(setOf(symbol('3'), symbol('-'), DigitSymbol)), parse("""[3-\d]"""))
        assertEquals(SetNotationSymbol(setOf(DigitSymbol, symbol('-'), DigitSymbol)), parse("""[\d-\d]"""))
    }

    @Test
    fun setNotationMetacharacterThatDoNotRequireEscaping() {
        assertEquals(SetNotationSymbol(symbols('x', '+')), parse("""[x+]"""))
        assertEquals(SetNotationSymbol(symbols('x', '*')), parse("""[x*]"""))
        assertEquals(SetNotationSymbol(symbols('x', '?')), parse("""[x?]"""))
        assertEquals(SetNotationSymbol(symbols('x', '.')), parse("""[x.]"""))
        assertEquals(SetNotationSymbol(symbols('x', ' ')), parse("""[x ]"""))
        assertEquals(SetNotationSymbol(symbols('x', 'y', '|')), parse("""[x|y]"""))
        assertEquals(SetNotationSymbol(symbols('x', '(', ')')), parse("""[(x)]"""))
        assertEquals(SetNotationSymbol(symbols('x', '{', '}', '1', '2', ',')), parse("""[x{1,2}]"""))
        assertEquals(SetNotationSymbol(symbols('x', '^')), parse("""[x^]"""))
        assertEquals(SetNotationSymbol(symbols('x', '$')), parse("""[x$]"""))
    }

    @Test
    fun setNotationSpecialCharacterEscaping() {
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', '\\', '0', '1', '2')), parse("""[abc\\012]"""))
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', ']', '0', '1', '2')), parse("""[abc\]012]"""))
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', '^', '0', '1', '2')), parse("""[abc\^012]"""))
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', '^', '0', '1', '2')), parse("""[\^abc012]"""))
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', '-', 'e', 'f', 'g')), parse("""[abc\-efg]"""))
    }

    @Test
    fun setNotationCaretInTheMiddleDoesNotCountedAsMetacharacter() {
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', '^', '0', '1', '2')), parse("""[abc^012]"""))
    }

    @Test
    fun setNotationNegation() {
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'd'), negated = true), parse("""[^abd]"""))
    }

    @Test
    fun shorthandCharacterClassesDigits() {
        assertEquals(DigitSymbol, parse("""\d"""))
        assertEquals(SetNotationSymbol(setOf(DigitSymbol)), parse("""[\d]"""))
    }

    @Test
    fun shorthandCharacterClassesWordCharacters() {
        assertEquals(WordSymbol, parse("""\w"""))
        assertEquals(SetNotationSymbol(setOf(WordSymbol)), parse("""[\w]"""))
    }

    @Test
    fun shorthandCharacterClassesWhitespaceCharacters() {
        assertEquals(WhitespaceSymbol, parse("""\s"""))
        assertEquals(SetNotationSymbol(setOf(WhitespaceSymbol)), parse("""[\s]"""))
    }

    @Test
    fun shorthandCharacterClassesNonDigits() {
        assertEquals(NonDigitSymbol, parse("""\D"""))
        assertEquals(SetNotationSymbol(setOf(NonDigitSymbol)), parse("""[\D]"""))
    }

    @Test
    fun shorthandCharacterClassesNonWordCharacters() {
        assertEquals(NonWordSymbol, parse("""\W"""))
        assertEquals(SetNotationSymbol(setOf(NonWordSymbol)), parse("""[\W]"""))
    }

    @Test
    fun shorthandCharacterClassesNonWhitespaceCharacters() {
        assertEquals(NonWhitespaceSymbol, parse("""\S"""))
        assertEquals(SetNotationSymbol(setOf(NonWhitespaceSymbol)), parse("""[\S]"""))
    }

    @Test
    fun hexadecimalCharacters() {
        assertEquals(symbol('>'), parse("""\x3e"""))
        assertEquals(symbol('/'), parse("""\x2F"""))
        assertEquals(SetNotationSymbol(symbols('1', '3', '5')), parse("""[\x31\x33\x35]"""))
        assertEquals(SetNotationSymbol(symbols('>', ' ', '/')), parse("""[\x3e\x20\x2F]"""))
    }

    @Test
    fun hexadecimalNotationEndsAbruptly() {
        assertFailsWith<ParseException> {
            parse("""abc\x""")
        }.let {
            assertEquals(ParseException("""Expected hexadecimal digit but got end of input""", 5), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\x1""")
        }.let {
            assertEquals(ParseException("""Expected hexadecimal digit but got end of input""", 6), it)
        }
    }

    @Test
    fun hexadecimalNotationNotANumber() {
        assertFailsWith<ParseException> {
            parse("""abc\xNO""")
        }.let {
            assertEquals(ParseException("Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got 'N'", 5), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\x1g""")
        }.let {
            assertEquals(ParseException("Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got 'g'", 6), it)
        }
    }

    @Test
    fun unicodeCharacters() {
        assertEquals(symbol('1'), parse("""\u0031"""))
        assertEquals(SetNotationSymbol(symbols('1', '3', '5')), parse("""[\u0031\u0033\u0035]"""))
        assertEquals(SetNotationSymbol(symbols('1', '3', '5', 'a', 'b')), parse("""[\u0031\u0033ab\u0035]"""))
        assertEquals(symbol('დ'), parse("""\u10d3"""))
        assertEquals(symbol('დ'), parse("""\u10D3"""))
        assertEquals(SetNotationSymbol(symbols('დ', '1', '2', '3')), parse("""[\u10d3123]"""))
    }

    @Test
    fun unicodeCharactersEndsAbruptly() {
        assertFailsWith<ParseException> {
            parse("""abc\u""")
        }.let {
            assertEquals(ParseException("""Expected hexadecimal digit but got end of input""", 5), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\u0""")
        }.let {
            assertEquals(ParseException("""Expected hexadecimal digit but got end of input""", 6), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\u00""")
        }.let {
            assertEquals(ParseException("""Expected hexadecimal digit but got end of input""", 7), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\u002""")
        }.let {
            assertEquals(ParseException("""Expected hexadecimal digit but got end of input""", 8), it)
        }
    }

    @Test
    fun unicodeCharactersNotANumber() {
        assertFailsWith<ParseException> {
            parse("""abc\uNO""")
        }.let {
            assertEquals(ParseException("Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got 'N'", 5), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\u0NO""")
        }.let {
            assertEquals(ParseException("Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got 'N'", 6), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\u00NO""")
        }.let {
            assertEquals(ParseException("Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got 'N'", 7), it)
        }
        assertFailsWith<ParseException> {
            parse("""abc\u002NO""")
        }.let {
            assertEquals(ParseException("Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got 'N'", 8), it)
        }
    }

    @Test
    fun freeSpaceModeWhitespacesIgnored() {
        assertEquals(concatenation(symbol('a'), symbol('b'), symbol('c')), parse("a\rb c"))
    }

    @Test
    fun freeSpaceModeWhitespacesNotIgnoredInSetNotation() {
        assertEquals(SetNotationSymbol(symbols('a', 'b', 'c', ' ')), parse("""[a b c]"""))
    }

    @Test
    fun freeSpaceModeEscapedWhitespace() {
        assertEquals(concatenation(symbol(' '), symbol('d')), parse("""\ d"""))
    }

    @Test
    fun freeSpaceModeWithRepeatNotation() {
        assertEquals(Repeat(symbol('a'), 0, null), parse("a \t *"))
        assertEquals(Repeat(symbol('a'), 1, null), parse("a \t +"))
        assertEquals(Repeat(symbol('a'), 0, 1), parse("a \t ?"))

        assertEquals(Repeat(symbol('a'), 13, 13), parse("a{ \t 13}"))
        assertEquals(Repeat(symbol('a'), 13, 13), parse("a{13 \t }"))

        assertEquals(Repeat(symbol('a'), 13, null), parse("a{13, \t }"))

        assertEquals(Repeat(symbol('a'), 13, 22), parse("a{ \t 13,22}"))
        assertEquals(Repeat(symbol('a'), 13, 22), parse("a{13 \t ,22}"))
        assertEquals(Repeat(symbol('a'), 13, 22), parse("a{13, \t 22}"))
        assertEquals(Repeat(symbol('a'), 13, 22), parse("a{13,22 \t }"))
    }

    @Test
    fun startOfLineAnchor() {
        assertEquals(StartOfLine, parse("""^"""))
        assertEquals(concatenation(StartOfLine, symbol('a'), symbol('b')), parse("""^ab"""))
        assertEquals(concatenation(symbol('a'), StartOfLine, symbol('b')), parse("""a^b"""))
    }

    @Test
    fun endOfLineAnchor() {
        assertEquals(EndOfLine, parse("""$"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), EndOfLine), parse("""ab$"""))
        assertEquals(concatenation(symbol('a'), EndOfLine, symbol('b')), parse("a\$b"))
    }

    @Test
    fun startOfStringAnchor() {
        assertEquals(StartOfString, parse("""\A"""))
        assertEquals(concatenation(StartOfString, symbol('a'), symbol('b')), parse("""\Aab"""))
        assertEquals(concatenation(symbol('a'), StartOfString, symbol('b')), parse("""a\Ab"""))
        assertEquals(SetNotationSymbol(setOf(symbol('a'), StartOfString, symbol('b'))), parse("""[a\Ab]"""))
    }

    @Test
    fun endOfStringAnchor() {
        assertEquals(EndOfString, parse("""\Z"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), EndOfString), parse("""ab\Z"""))
        assertEquals(concatenation(symbol('a'), EndOfString, symbol('b')), parse("""a\Zb"""))
        assertEquals(SetNotationSymbol(setOf(symbol('a'), EndOfString, symbol('b'))), parse("""[a\Zb]"""))

    }

    @Test
    fun endOfStringOnlyAnchor() {
        assertEquals(EndOfStringOnly, parse("""\z"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), EndOfStringOnly), parse("""ab\z"""))
        assertEquals(concatenation(symbol('a'), EndOfStringOnly, symbol('b')), parse("""a\zb"""))
        assertEquals(SetNotationSymbol(setOf(symbol('a'), EndOfStringOnly, symbol('b'))), parse("""[a\zb]"""))
    }

    @Test
    fun previousMatchAnchor() {
        assertEquals(PreviousMatch, parse("""\G"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), PreviousMatch), parse("""ab\G"""))
        assertEquals(concatenation(symbol('a'), PreviousMatch, symbol('b')), parse("""a\Gb"""))
        assertEquals(SetNotationSymbol(setOf(symbol('a'), PreviousMatch, symbol('b'))), parse("""[a\Gb]"""))
    }

    @Test
    fun wordBoundaryAnchor() {
        assertEquals(WordBoundary, parse("""\b"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), WordBoundary), parse("""ab\b"""))
        assertEquals(concatenation(symbol('a'), WordBoundary, symbol('b')), parse("""a\bb"""))
    }

    @Test
    fun nonWordBoundaryAnchor() {
        assertEquals(NonWordBoundary, parse("""\B"""))
        assertEquals(concatenation(symbol('a'), symbol('b'), NonWordBoundary), parse("""ab\B"""))
        assertEquals(concatenation(symbol('a'), NonWordBoundary, symbol('b')), parse("""a\Bb"""))
        assertEquals(SetNotationSymbol(setOf(symbol('a'), NonWordBoundary, symbol('b'))), parse("""[a\Bb]"""))
    }

    @Test
    fun lazyRepeats() {
        assertEquals(Repeat(symbol('a'), 0, null, Repeat.Type.LAZY), parse("""a*?"""))
        assertEquals(Repeat(symbol('a'), 1, null, Repeat.Type.LAZY), parse("""a+?"""))
        assertEquals(Repeat(symbol('a'), 0, 1, Repeat.Type.LAZY), parse("""a??"""))
        assertEquals(Repeat(symbol('a'), 2, 2, Repeat.Type.LAZY), parse("""a{2}?"""))
        assertEquals(Repeat(symbol('a'), 2, null, Repeat.Type.LAZY), parse("""a{2,}?"""))
        assertEquals(Repeat(symbol('a'), 2, 5, Repeat.Type.LAZY), parse("""a{2,5}?"""))
    }

    @Test
    fun possessiveRepeats() {
        assertEquals(Repeat(symbol('a'), 0, null, Repeat.Type.POSSESSIVE), parse("""a*+"""))
        assertEquals(Repeat(symbol('a'), 1, null, Repeat.Type.POSSESSIVE), parse("""a++"""))
        assertEquals(Repeat(symbol('a'), 0, 1, Repeat.Type.POSSESSIVE), parse("""a?+"""))
        assertEquals(Repeat(symbol('a'), 2, 2, Repeat.Type.POSSESSIVE), parse("""a{2}+"""))
        assertEquals(Repeat(symbol('a'), 2, null, Repeat.Type.POSSESSIVE), parse("""a{2,}+"""))
        assertEquals(Repeat(symbol('a'), 2, 5, Repeat.Type.POSSESSIVE), parse("""a{2,5}+"""))
    }

    @Test
    fun operatorPrioritiesMoreComplexTest1() {
        assertEquals(
            union(concatenation("abc"), symbol('d'), concatenation("efg")),
            parse("""abc|d|efg""")
        )
    }

    @Test
    fun operatorPrioritiesMoreComplexTest2() {
        assertEquals(
            union(symbol('a'), symbol('b'), concatenation("cde"), symbol('f'), symbol('g')),
            parse("""a|b|cde|f|g""")
        )
    }

    @Test
    fun operatorPrioritiesMoreComplexTest3() {
        assertEquals(
            union(
                concatenation("ab"),
                concatenation(
                    symbol('c'),
                    Repeat(symbol('d'), 1, null),
                    symbol('e'),
                    Repeat(symbol('f'), 0, null),
                    symbol('g')
                )
            ),
            parse("""ab|cd+ef*g""")
        )
    }

    @Test
    fun operatorPrioritiesMoreComplexTest4() {
        assertEquals(
            union(
                concatenation("ab"),
                concatenation(
                    symbol('c'),
                    Repeat(symbol('d'), 1, null),
                    symbol('e')
                ),
                concatenation(
                    Repeat(symbol('f'), 0, null),
                    symbol('g')
                )
            ),
            parse("""ab|cd+e|f*g""")
        )
    }

    @Test
    fun operatorPrioritiesMoreComplexTest5() {
        assertEquals(
            concatenation(
                union(
                    symbol('a'),
                    symbol('b'),
                    symbol('c')
                ),
                symbol('d'),
                union(
                    symbol('e'),
                    symbol('f'),
                    symbol('g')
                )
            ),
            parse("""(a|b|c)d(e|f|g)""")
        )
    }

    @Test
    fun operatorPrioritiesMoreComplexTest6() {
        val expected = union(
            symbol('a'),
            concatenation(
                union(symbol('b'), symbol('c')),
                symbol('d'),
                symbol('e')
            ),
            symbol('f'),
            symbol('g')
        )
        assertEquals(expected, parse("""a|(b|c)de|(f)|g"""))
        assertEquals(expected, parse("""(a|(b|c)de|(f)|g)"""))
        assertEquals(expected, parse("""a|((b|c)de)|(f)|g"""))
        assertEquals(expected, parse("""a|((b|c)de|(f))|g"""))
        assertEquals(expected, parse("""a|(b|c)de|((f)|g)"""))
        assertEquals(expected, parse("""a|((b|c)de|(f)|g)"""))
        assertEquals(expected, parse("""((((a|(((b|(c)))de))|((f)|g))))"""))
    }

    @Test
    fun operatorPrioritiesMoreComplexTest7() {
        val expected = union(
            symbol('a'),
            concatenation(
                Repeat(union(Repeat(symbol('b'), 2, null), symbol('c')), 0, null),
                symbol('d'),
                Repeat(symbol('e'), 0, 1)
            ),
            Repeat(Repeat(symbol('f'), 1, null), 1, null),
            symbol('g')
        )
        assertEquals(expected, parse("""a|(b{2,}|c)*de?|(f+)+|g"""))
        assertEquals(expected, parse("""(a|(b{2,}|c)*de?|(f+)+|g)"""))
        assertEquals(expected, parse("""a|((b{2,}|c)*de?)|(f+)+|g"""))
        assertEquals(expected, parse("""a|((b{2,}|c)*de?|(f+)+)|g"""))
        assertEquals(expected, parse("""a|(b{2,}|c)*de?|((f+)+|g)"""))
        assertEquals(expected, parse("""a|((b{2,}|c)*de?|(f+)+|g)"""))
        assertEquals(expected, parse("""((((a|(((b{2,}|(c))*)d(e)?))|((f+)+|g))))"""))
    }

    @Test
    fun numbersFrom0To255_1() {
        val digits = anySymbol('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        assertEquals(
            union(
                concatenation(symbol('2'), symbol('5'), anySymbol('0', '1', '2', '3', '4', '5')),
                concatenation(symbol('2'), anySymbol('0', '1', '2', '3', '4'), digits),
                concatenation(symbol('1'), digits, digits),
                concatenation(Repeat(anySymbol('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 1), digits),
            ), parse(
                """25[0-5]|
            2[0-4][0-9]|
            1[0-9][0-9]|
            [1-9]?[0-9]"""
            )
        )
    }

    @Test
    fun numbersFrom0To255_2() {
        assertEquals(
            union(
                concatenation(symbol('2'), symbol('5'), anySymbol('0', '1', '2', '3', '4', '5')),
                concatenation(symbol('2'), anySymbol('0', '1', '2', '3', '4'), DigitSymbol),
                concatenation(symbol('1'), DigitSymbol, DigitSymbol),
                concatenation(Repeat(anySymbol('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 1), DigitSymbol),
            ), parse(
                """25[0-5]|
            2[0-4]\d|
            1\d\d|
            [1-9]?\d"""
            )
        )
    }

    @Test
    fun ipV4regex_1() {
        val digits = anySymbol('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        assertEquals(
            concatenation(
                Repeat(
                    concatenation(
                        union(
                            concatenation(symbol('2'), symbol('5'), anySymbol('0', '1', '2', '3', '4', '5')),
                            concatenation(symbol('2'), anySymbol('0', '1', '2', '3', '4'), digits),
                            concatenation(symbol('1'), digits, digits),
                            concatenation(
                                Repeat(anySymbol('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 1),
                                digits
                            ),
                        ),
                        symbol('.')
                    ), 3, 3
                ),
                union(
                    concatenation(symbol('2'), symbol('5'), anySymbol('0', '1', '2', '3', '4', '5')),
                    concatenation(symbol('2'), anySymbol('0', '1', '2', '3', '4'), digits),
                    concatenation(symbol('1'), digits, digits),
                    concatenation(
                        Repeat(anySymbol('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 1),
                        digits
                    ),
                )
            ), parse(
                """((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])"""
            )
        )
    }

    @Test
    fun ipV4regex_2() {
        assertEquals(
            concatenation(
                Repeat(
                    concatenation(
                        union(
                            concatenation(symbol('2'), symbol('5'), anySymbol('0', '1', '2', '3', '4', '5')),
                            concatenation(symbol('2'), anySymbol('0', '1', '2', '3', '4'), DigitSymbol),
                            concatenation(symbol('1'), DigitSymbol, DigitSymbol),
                            concatenation(
                                Repeat(anySymbol('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 1),
                                DigitSymbol
                            ),
                        ),
                        symbol('.')
                    ), 3, 3
                ),
                union(
                    concatenation(symbol('2'), symbol('5'), anySymbol('0', '1', '2', '3', '4', '5')),
                    concatenation(symbol('2'), anySymbol('0', '1', '2', '3', '4'), DigitSymbol),
                    concatenation(symbol('1'), DigitSymbol, DigitSymbol),
                    concatenation(
                        Repeat(anySymbol('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 1),
                        DigitSymbol
                    ),
                )
            ), parse(
                """((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)"""
            )
        )
    }

    @Test
    fun emailRegexSimple() {
        assertEquals(
            concatenation(
                StartOfLine,
                Repeat(
                    anySymbol(
                        'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i',
                        'J', 'j', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r',
                        'S', 's', 'T', 't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                        '_', '.', '+', '-'
                    ), 1, null
                ),
                symbol('@'),
                Repeat(
                    anySymbol(
                        'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i',
                        'J', 'j', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r',
                        'S', 's', 'T', 't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                        '-'
                    ), 1, null
                ),
                symbol('.'),
                Repeat(
                    anySymbol(
                        'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i',
                        'J', 'j', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r',
                        'S', 's', 'T', 't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                        '-', '.'
                    ), 1, null
                ),
                EndOfLine
            ), parse(
                """^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+${'$'}"""
            )
        )
    }

    @Test
    fun crossTestWithKotlinParser() {
        parse("""[()*?|a\\]{0,5}""")
            .toEpsilonNFA(setOf('(', ')', '*', '|') + ('a'..'z').toSet())
            .toNFA()
            .toDFA()
            .allStringsAlphabetically()
            .forEach { regex ->
                val expected = try {
                    Regex(regex)
                    true
                } catch (e: IllegalArgumentException) {
                    false
                }
                val actual = try {
                    parse(regex)
                    true
                } catch (e: ParseException) {
                    false
                }
                assertEquals(expected, actual, regex)
            }
    }

    private fun assertException(regexes: Set<String>, expectedException: Exception) = regexes.forEach { regex ->
        assertEquals(expectedException, assertFailsWith<ParseException> { parse(regex) })
        assertFailsWith<IllegalArgumentException> { Regex(regex).matches("") }
    }
}
