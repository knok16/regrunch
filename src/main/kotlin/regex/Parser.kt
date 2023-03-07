package regex

import java.util.*

class ParseException constructor(message: String, val fromIndex: Int, val toIndex: Int) : Exception(message) {
    constructor(message: String, at: Int) : this(message, at, at)
}

internal class Reader(private val str: String) {
    private var i = 0
    fun next(): Char? = if (i < str.length) str[i++] else null
    fun cursor(): Int = i
    fun prevCursor(): Int = i - 1 // TODO ?
}

internal fun parseEscapedCharacter(reader: Reader, alphabet: Set<Char>): SymbolToken {
    val cursor = reader.cursor()
    val symbol = when (val char = reader.next()) {
        null -> throw ParseException("No character to escape", reader.prevCursor())
        'd' -> Symbol(alphabet.filter { it.isDigit() }.toSet())
        'D' -> Symbol(alphabet.filterNot { it.isDigit() }.toSet())
        's' -> Symbol(alphabet.filter { it.isWhitespace() }.toSet())
        'S' -> Symbol(alphabet.filterNot { it.isWhitespace() }.toSet())
        'w' -> Symbol(alphabet.filter { it.isLetter() || it.isDigit() || it == '_' }.toSet())
        'W' -> Symbol(alphabet.filterNot { it.isLetter() || it.isDigit() || it == '_' }.toSet())
        't' -> Symbol(setOf('\t')) // TODO assert that it is in alphabet
        'r' -> Symbol(setOf('\r'))
        'n' -> Symbol(setOf('\n'))
        'v' -> Symbol(setOf(0x0B.toChar()))
        'a' -> Symbol(setOf(0x07.toChar()))
        'e' -> Symbol(setOf(0x1B.toChar()))
        'f' -> Symbol(setOf(0x0C.toChar()))
        'x' -> TODO("Hexadecimal")
        'u' -> TODO("Unicode")
        else -> Symbol(setOf(char))
    }
    return SymbolToken(symbol, cursor, cursor)
}

internal fun parseSetNotation(reader: Reader, alphabet: Set<Char>): SymbolToken {
    val initialCursor = reader.prevCursor()
    val characters = HashSet<Char>()
    while (true) {
        val char = reader.next() ?: throw ParseException("Unbalanced square bracket", initialCursor)
        when (char) {
            ']' -> break
            '\\' -> characters.addAll(parseEscapedCharacter(reader, alphabet).symbol.options)
            else -> characters.add(char)
        }
    }

    return SymbolToken(Symbol(characters), initialCursor, reader.prevCursor())
}

// TODO rework!
internal fun parseRepeatNotation(reader: Reader): RepeatOperator {
    val initialCursor = reader.prevCursor()
    val s = StringBuilder()
    while (true) {
        val char = reader.next() ?: throw ParseException("Unbalanced curly bracket", initialCursor)
        if (char == '}') break else s.append(char)
    }
    val ints = s.toString().split(',').map { it.trim() }.map { it.takeIf { it.isNotBlank() } }
        .map { it?.toInt() } // TODO add error handling
    val endingCursor = reader.prevCursor()
    return when (ints.size) {
        0 -> RepeatOperator(0, null, initialCursor, endingCursor) // TODO is it legal?
        1 -> RepeatOperator(ints[0] ?: 0, ints[0], initialCursor, endingCursor) // TODO think about ?:0
        2 -> RepeatOperator(ints[0] ?: 0, ints[1], initialCursor, endingCursor)
        else -> throw ParseException("Unexpected number of parts in repeat operator", initialCursor, endingCursor)
    }
}

// TODO add indexes, for error reporting
internal sealed interface Token
internal data class UnionOperator(val at: Int) : Token
internal data class ConcatenationOperator(val at: Int) : Token // TODO what to do here?
internal data class RepeatOperator(val min: Int, val max: Int?, val fromIndex: Int, val toIndex: Int) : Token
internal data class SymbolToken(val symbol: Symbol, val fromIndex: Int, val toIndex: Int) : Token
internal data class LeftBracket(val at: Int) : Token
internal data class RightBracket(val at: Int) : Token

internal fun tokenize(str: String, alphabet: Set<Char>): List<Token> {
    val reader = Reader(str)

    val result = ArrayList<Token>()

    while (true) {
        val cursor = reader.cursor()
        val char = reader.next() ?: break
        val token = when (char) {
            '\\' -> parseEscapedCharacter(reader, alphabet)
            '[' -> parseSetNotation(reader, alphabet)
            '|' -> UnionOperator(cursor)
            '*' -> RepeatOperator(0, null, cursor, cursor)
            '+' -> RepeatOperator(1, null, cursor, cursor)
            '?' -> RepeatOperator(0, 1, cursor, cursor)
            '{' -> parseRepeatNotation(reader)
            '(' -> LeftBracket(cursor)
            ')' -> RightBracket(cursor)
            '.' -> SymbolToken(Symbol(alphabet), cursor, cursor)
            else -> SymbolToken(Symbol(setOf(char)), cursor, cursor)
        }

        if (result.isNotEmpty() && (result.last() is SymbolToken || result.last() is RightBracket) && (token is SymbolToken || token is LeftBracket))
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
    val tokens = tokenize(str, (0..127).map { it.toChar() }.toSet()) // TODO accept as parameter

    val results = Stack<RegexPart>()
    val operatorStack = Stack<Token>()

    fun applyOperator(operator: Token) {
        when (operator) {
            is RepeatOperator -> {
                if (results.isEmpty()) {
                    throw ParseException("No operand", operator.fromIndex, operator.toIndex)
                }
                results.push(Repeat(results.pop(), operator.min, operator.max))
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
