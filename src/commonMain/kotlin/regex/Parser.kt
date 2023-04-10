package regex

import utils.Stack
import utils.isEmpty
import utils.isNotEmpty

data class ParseException(override val message: String, val fromIndex: Int, val toIndex: Int) : Exception(message) {
    constructor(message: String, at: Int) : this(message, at, at)
}

internal class Reader(private val str: String) {
    private var i = 0
    fun next(): Char? = if (i < str.length) str[i++] else null
    fun peek(): Char? = if (i < str.length) str[i] else null
    fun cursor(): Int = i
    fun prevCursor(): Int = i - 1 // TODO ?
}

internal fun Reader.readHexDigit(): Int =
    when (val char = next() ?: throw ParseException("Expected hexadecimal digit but got end of input", cursor())) {
        in '0'..'9' -> char - '0'
        in 'A'..'F' -> char - 'A' + 10
        in 'a'..'f' -> char - 'a' + 10
        else -> throw ParseException(
            "Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got '$char'",
            prevCursor()
        )
    }

internal fun Reader.readRepeatType(): Repeat.Type = when (peek()) {
    '?' -> {
        next()
        Repeat.Type.LAZY
    }

    '+' -> {
        next()
        Repeat.Type.POSSESSIVE
    }

    else -> Repeat.Type.GREEDY
}

internal fun parseEscapedCharacter(reader: Reader, forSetNotation: Boolean): Symbol =
    when (val char = reader.next()) {
        null -> throw ParseException("No character to escape", reader.prevCursor())
        'd' -> DigitSymbol
        'D' -> NonDigitSymbol
        's' -> WhitespaceSymbol
        'S' -> NonWhitespaceSymbol
        'w' -> WordSymbol
        'W' -> NonWordSymbol
        't' -> ExactSymbol('\t')
        'r' -> ExactSymbol('\r')
        'n' -> ExactSymbol('\n')
        'v' -> ExactSymbol(0x0B.toChar())
        'a' -> ExactSymbol(0x07.toChar())
        'b' -> if (forSetNotation) ExactSymbol(0x10.toChar()) else WordBoundary
        'e' -> ExactSymbol(0x1B.toChar())
        'f' -> ExactSymbol(0x0C.toChar())
        'A' -> StartOfString
        'Z' -> EndOfString
        'z' -> EndOfStringOnly
        'G' -> PreviousMatch
        'B' -> NonWordBoundary
        'x' -> ExactSymbol((reader.readHexDigit() * 0x10 + reader.readHexDigit()).toChar())
        'u' -> ExactSymbol((1..4).map { reader.readHexDigit() }.reduce { acc, digit -> acc * 0x10 + digit }.toChar())
        'c' -> {
            val control = reader.next() ?: throw ParseException("No control character", reader.prevCursor())
            val code = when (control) {
                in 'A'..'Z' -> (control - 'A' + 1)
                in 'a'..'z' -> (control - 'a' + 1)
                else -> throw ParseException(
                    "Unexpected control character '$control' (only 'A'-'Z' or 'a'-'z' allowed)",
                    reader.prevCursor()
                )
            }
            ExactSymbol(code.toChar())
        }

        else -> ExactSymbol(char)
    }

internal fun parseSetNotation(reader: Reader): SetNotationSymbol {
    val initialCursor = reader.prevCursor()
    val symbols = HashSet<Symbol>()
    val negate = reader.peek() == '^'
    if (negate) reader.next()
    while (true) {
        val symbol =
            when (val char = reader.next() ?: throw ParseException("Unbalanced square bracket", initialCursor)) {
                ']' -> break
                '\\' -> parseEscapedCharacter(reader, true)
                else -> ExactSymbol(char)
            }

        if (reader.peek() == '-') {
            reader.next() // pop '-' character
            val to =
                when (val char = reader.next() ?: throw ParseException("Unbalanced square bracket", initialCursor)) {
                    ']' -> {
                        symbols.add(symbol)
                        symbols.add(ExactSymbol('-'))
                        break
                    }

                    '\\' -> parseEscapedCharacter(reader, true)
                    else -> ExactSymbol(char)
                }
            if (symbol is ExactSymbol && to is ExactSymbol) {
                (symbol.value..to.value).map(::ExactSymbol).forEach(symbols::add)
            } else {
                symbols.add(symbol)
                symbols.add(ExactSymbol('-'))
                symbols.add(to)
            }
        } else {
            symbols.add(symbol)
        }
    }

    return SetNotationSymbol(symbols, negate)
}

internal fun Reader.readDecimalDigit(): Int =
    when (val char = next() ?: throw ParseException("Expected decimal digit but got end of input", cursor())) {
        in '0'..'9' -> char - '0'
        else -> throw ParseException("Expected decimal digit but got '$char'", prevCursor())
    }

internal fun Reader.readDecimalNumber(): Int {
    var result = readDecimalDigit()
    while (peek() in '0'..'9') {
        result = result * 10 + readDecimalDigit()
    }
    return result
}

internal fun Reader.skipWhitespaces() {
    while (peek()?.isWhitespace() == true) next()
}

internal fun parseRepeatNotation(reader: Reader): RepeatOperator {
    val initialCursor = reader.prevCursor()

    reader.skipWhitespaces()
    val min = reader.readDecimalNumber()
    reader.skipWhitespaces()

    val max = when (val delimiter = reader.next()) {
        null -> throw ParseException("Unbalanced curly bracket", initialCursor)
        ',' -> {
            reader.skipWhitespaces()
            if (reader.peek() == '}') {
                reader.next()
                null
            } else {
                val t = reader.readDecimalNumber()
                reader.skipWhitespaces()
                when (val char = reader.next()) {
                    null -> throw ParseException("Unbalanced curly bracket", initialCursor)
                    '}' -> t
                    else -> throw ParseException("Expected '}' but got '$char'", reader.prevCursor())
                }
            }
        }

        '}' -> min
        else -> throw ParseException("Expected ',' or '}', but got '$delimiter'", reader.prevCursor())
    }

    if (max != null && max < min) throw ParseException(
        "Min number of repeats ($min) should be less or equals than max ($max) number of repeats",
        initialCursor,
        reader.prevCursor()
    )

    return RepeatOperator(min, max, reader.readRepeatType(), initialCursor, reader.prevCursor())
}

internal sealed interface Token
internal data class UnionOperator(val at: Int) : Token
internal data class ConcatenationOperator(val at: Int) : Token // TODO what to do here?
internal data class RepeatOperator(
    val min: Int,
    val max: Int?,
    val type: Repeat.Type,
    val fromIndex: Int,
    val toIndex: Int
) : Token

internal data class SymbolToken(val symbol: Symbol) : Token
internal data class LeftBracket(val at: Int) : Token
internal data class RightBracket(val at: Int) : Token

internal fun tokenize(str: String): List<Token> {
    val reader = Reader(str)

    val result = ArrayList<Token>()

    while (true) {
        val cursor = reader.cursor()
        val char = reader.next() ?: break
        val token = when (char) {
            '\\' -> SymbolToken(parseEscapedCharacter(reader, false))
            '[' -> SymbolToken(parseSetNotation(reader))
            '|' -> UnionOperator(cursor)
            '*' -> RepeatOperator(0, null, reader.readRepeatType(), cursor, cursor)
            '+' -> RepeatOperator(1, null, reader.readRepeatType(), cursor, cursor)
            '?' -> RepeatOperator(0, 1, reader.readRepeatType(), cursor, cursor)
            '{' -> parseRepeatNotation(reader)
            '(' -> LeftBracket(cursor)
            ')' -> RightBracket(cursor)
            '.' -> SymbolToken(AnySymbol)
            '^' -> SymbolToken(StartOfLine)
            '$' -> SymbolToken(EndOfLine)
            else -> if (char.isWhitespace()) continue else SymbolToken(ExactSymbol(char))
        }

        if (result.isNotEmpty() &&
            (result.last() is RepeatOperator || result.last() is SymbolToken || result.last() is RightBracket) &&
            (token is SymbolToken || token is LeftBracket)
        )
            result.add(ConcatenationOperator(cursor))
        result.add(token)
    }

    return result
}

internal fun concatenate(left: RegexPart, right: RegexPart): RegexPart = Concatenation(
    (if (left is Concatenation) left.parts else listOf(left)) +
            (if (right is Concatenation) right.parts else listOf(right))
)

internal fun union(left: RegexPart, right: RegexPart): RegexPart = Union(
    (if (left is Union) left.parts else setOf(left)) +
            (if (right is Union) right.parts else setOf(right))
)

fun parse(str: String): RegexPart {
    val tokens = tokenize(str)

    val results = Stack<RegexPart>()
    val operatorStack = Stack<Token>()

    fun applyOperator(operator: Token) {
        when (operator) {
            is RepeatOperator -> {
                if (results.isEmpty()) {
                    throw ParseException("No operand", operator.fromIndex, operator.toIndex)
                }
                results.push(Repeat(results.pop(), operator.min, operator.max, operator.type))
            }

            is ConcatenationOperator -> {
                val operand2 = results.pop() // TODO add error handling?
                val operand1 = results.pop() // TODO add error handling?

                results.push(concatenate(operand1, operand2))
            }

            is UnionOperator -> {
                val operand2 = results.pop() // TODO add error handling?
                val operand1 = results.pop() // TODO add error handling?
                results.push(union(operand1, operand2))
            }

            else -> throw IllegalArgumentException() // TODO
        }
    }

    for (token in tokens) {
        when (token) {
            is UnionOperator -> {
                while (operatorStack.isNotEmpty() && (operatorStack.peek() is ConcatenationOperator || operatorStack.peek() is UnionOperator)) {
                    applyOperator(operatorStack.pop())
                }
                operatorStack.push(token)
            }

            is ConcatenationOperator -> {
                while (operatorStack.isNotEmpty() && operatorStack.peek() is ConcatenationOperator) {
                    applyOperator(operatorStack.pop())
                }
                operatorStack.push(token)
            }

            is RepeatOperator -> applyOperator(token)
            is SymbolToken -> results.push(token.symbol)
            is LeftBracket -> operatorStack.push(token)
            is RightBracket -> {
                while (operatorStack.isNotEmpty() && operatorStack.peek() !is LeftBracket) {
                    applyOperator(operatorStack.pop())
                }
                if (operatorStack.isEmpty() || operatorStack.pop() !is LeftBracket) {
                    throw ParseException("Unbalanced right bracket", token.at)
                }
            }
        }
    }

    while (operatorStack.isNotEmpty()) {
        val token = operatorStack.pop()
        if (token is LeftBracket) {
            throw ParseException("Unbalanced left bracket", token.at)
        } else {
            applyOperator(token)
        }
    }

    return results.pop()
}
