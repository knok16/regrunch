package com.github.knok16.regrunch.regex

data class ParseException(override val message: String, val fromIndex: Int, val toIndex: Int) : Exception(message) {
    constructor(message: String, at: Int) : this(message, at, at)
}

private class Reader(private val str: String) {
    private var i = 0
    fun next(): Char? = if (i < str.length) str[i++] else null
    fun peek(): Char? = if (i < str.length) str[i] else null
    fun cursor(): Int = i
    fun prevCursor(): Int = i - 1 // TODO ?
}

private fun Reader.readHexDigit(): Int =
    when (val char = next() ?: throw ParseException("Expected hexadecimal digit but got end of input", cursor())) {
        in '0'..'9' -> char - '0'
        in 'A'..'F' -> char - 'A' + 10
        in 'a'..'f' -> char - 'a' + 10
        else -> throw ParseException(
            "Expected hexadecimal digit ('0'-'9', 'A'-'Z, 'a'-'z') but got '$char'",
            prevCursor()
        )
    }

private fun Reader.readRepeatType(): Repeat.Type = when (peek()) {
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

private fun parseEscapedCharacter(reader: Reader, forSetNotation: Boolean): Symbol =
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
        'v' -> VerticalWhitespaceSymbol
        'h' -> HorizontalWhitespaceSymbol
        'a' -> ExactSymbol(0x07.toChar())
        'b' -> if (forSetNotation) ExactSymbol(0x08.toChar()) else WordBoundary
        'e' -> ExactSymbol(0x1B.toChar())
        'f' -> ExactSymbol(0x0C.toChar())
        'A' -> StartOfString
        'Z' -> EndOfString
        'z' -> EndOfStringOnly
        'G' -> PreviousMatch
        'B' -> NonWordBoundary
        'x' -> ExactSymbol(((reader.readHexDigit() shl 4) or reader.readHexDigit()).toChar())
        'u' -> ExactSymbol((1..4).map { reader.readHexDigit() }.reduce { acc, digit -> (acc shl 4) or digit }.toChar())
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

private fun parseSetNotation(reader: Reader): SetNotationSymbol {
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

private fun Reader.readDecimalDigit(): Int =
    when (val char = next() ?: throw ParseException("Expected decimal digit but got end of input", cursor())) {
        in '0'..'9' -> char - '0'
        else -> throw ParseException("Expected decimal digit but got '$char'", prevCursor())
    }

private fun Reader.readDecimalNumber(): Int {
    var result = readDecimalDigit()
    while (peek() in '0'..'9') {
        result = result * 10 + readDecimalDigit()
    }
    return result
}

private fun Reader.skipWhitespaces() {
    while (peek()?.isWhitespace() == true) next()
}

private fun parseRepeatNotation(reader: Reader): RepeatOperator {
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

// TODO move from and to fields here?
private sealed interface Token
private data class UnionOperator(val at: Int) : Token
private data class RepeatOperator(
    val min: Int,
    val max: Int?,
    val type: Repeat.Type,
    val fromIndex: Int,
    val toIndex: Int
) : Token

private data class SymbolToken(val symbol: Symbol) : Token
private sealed interface LeftBracket : Token {
    val from: Int
    val to: Int
}

private data class NonCapturingGroupStart(override val from: Int, override val to: Int) : LeftBracket
private data class CapturingGroupStart(val groupNumber: Int, val at: Int) : LeftBracket {
    override val from: Int get() = at
    override val to: Int get() = at
}

private data class RightBracket(val at: Int) : Token

private fun tokenize(str: String): List<Token> {
    val reader = Reader(str)

    val result = ArrayList<Token>()
    var lastCapturingGroupNumber = 0

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
            '(' -> if (reader.peek() == '?') {
                reader.next() // pop question mark
                when (val next = reader.next()) {
                    ':' -> NonCapturingGroupStart(cursor, reader.prevCursor())
                    // TODO add support of lookahead/lookbehind and atomic groups
                    else -> throw ParseException("Expected ':', but got '$next'", reader.prevCursor())
                }
            } else CapturingGroupStart(++lastCapturingGroupNumber, cursor)

            ')' -> RightBracket(cursor)
            '.' -> SymbolToken(AnySymbol)
            '^' -> SymbolToken(StartOfLine)
            '$' -> SymbolToken(EndOfLine)
            else -> if (char.isWhitespace()) continue else SymbolToken(ExactSymbol(char))
        }

        result.add(token)
    }

    return result
}

private class RecursiveDescentParser(private val tokens: List<Token>) {
    private var cursor: Int = 0

    fun parse(): RegexPart {
        val result = parsePartsUnion()
        return when (val it = peek()) {
            null -> result
            is RightBracket -> throw ParseException("Unbalanced right bracket", it.at)
            is RepeatOperator -> throw ParseException("No operand for repeat operator", it.fromIndex, it.toIndex)
            else -> throw RuntimeException("Unexpected token in queue, got '$it'")
        }
    }

    private fun peek(): Token? = if (cursor in tokens.indices) tokens[cursor] else null

    private fun next(): Token? = if (cursor in tokens.indices) tokens[cursor++] else null

    private fun parsePartsUnion(): RegexPart {
        val parts = mutableSetOf(parsePartsConcatenation())
        while (peek() is UnionOperator) {
            next() // pop union operator
            parts.add(parsePartsConcatenation())
        }
        return parts.singleOrNull() ?: Union(parts.flatMap { if (it is Union) it.parts else listOf(it) }.toSet())
    }

    private fun parsePartsConcatenation(): RegexPart {
        val parts = mutableListOf<RegexPart>()

        while (peek() is SymbolToken || peek() is LeftBracket) parts.add(parsePartsRepeats())

        return parts.singleOrNull()
            ?: Concatenation(parts.flatMap { if (it is Concatenation) it.parts else listOf(it) })
    }

    private fun parsePartsRepeats(): RegexPart {
        val part = parseSingleSymbolOrRegexInBrackets()
        return (peek() as? RepeatOperator)?.let { operator ->
            next() // pop repeat operator
            Repeat(part, operator.min, operator.max, operator.type)
        } ?: part
    }

    private fun parseSingleSymbolOrRegexInBrackets(): RegexPart {
        return when (val next = next()) {
            is SymbolToken -> next.symbol
            is LeftBracket -> {
                val result = parsePartsUnion()
                when (val p = next()) {
                    is RightBracket -> when (next) {
                        is NonCapturingGroupStart -> result
                        is CapturingGroupStart -> CaptureGroup(result, next.groupNumber)
                    }

                    is RepeatOperator -> throw ParseException("No operand for repeat operator", p.fromIndex, p.toIndex)
                    else -> throw ParseException("Unbalanced left bracket", next.from, next.to)
                }
            }

            else -> throw RuntimeException("Unexpected token in queue, got '$next'")
        }
    }
}

fun parse(str: String): RegexPart =
    RecursiveDescentParser(tokenize(str)).parse()
