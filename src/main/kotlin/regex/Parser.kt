package regex

import java.util.*

internal class Reader(private val str: String) {
    private var i = 0
    fun next(): Char? = if (i < str.length) str[i++] else null
}

internal fun parseEscapedCharacter(reader: Reader, alphabet: Set<Char>): Symbol {
    val char = reader.next()
    return when (char) {
        null -> TODO()
        'd' -> Symbol(alphabet.filter { it.isDigit() }.toSet())
        'D' -> Symbol(alphabet.filterNot { it.isDigit() }.toSet())
        's' -> Symbol(alphabet.filter { it.isWhitespace() }.toSet())
        'S' -> Symbol(alphabet.filterNot { it.isWhitespace() }.toSet())
        'w' -> Symbol(alphabet.filter { it.isLetter() || it.isDigit() || it == '_' }.toSet())
        'W' -> Symbol(alphabet.filterNot { it.isLetter() || it.isDigit() || it == '_' }.toSet())
        't' -> Symbol(setOf('\t')) // TODO assert that it is in alphabet
        'r' -> Symbol(setOf('\r'))
        'n' -> Symbol(setOf('\n'))
        '\\', '.', '|', '(', ')', '[', '{', '*', '+', '?' -> Symbol(setOf(char))
        else -> throw IllegalArgumentException("Unexpected escaped character '$char' at ") // TODO
    }
}

internal fun parseSetNotation(reader: Reader, alphabet: Set<Char>): Symbol = TODO()

// TODO rework!
internal fun parseRepeatNotation(reader: Reader): RepeatOperator {
    val s = StringBuilder()
    while (true) {
        val token = reader.next() ?: throw IllegalArgumentException("Unbalanced curly bracket at ${TODO()}")
        if (token == '}') break else s.append(token)
    }
    val ints = s.toString().split(',').map { it.trim() }.map { it.takeIf { it.isNotBlank() } }
        .map { it?.toInt() } // TODO add error handling
    return when (ints.size) {
        0 -> RepeatOperator(0, null) // TODO is it legal?
        1 -> RepeatOperator(ints[0] ?: 0, ints[0]) // TODO think about ?:0
        2 -> RepeatOperator(ints[0] ?: 0, ints[1])
        else -> throw IllegalArgumentException("${TODO()}")
    }
}

// TODO add indexes, for error reporting
internal sealed interface Token
object UnionOperator : Token
object ConcatenationOperator : Token
data class RepeatOperator(val min: Int, val max: Int?) : Token
data class SymbolToken(val symbol: Symbol) : Token
object LeftBracket : Token
object RightBracket : Token

internal fun tokenize(str: String, alphabet: Set<Char>): List<Token> {
    val reader = Reader(str)

    val result = ArrayList<Token>()

    while (true) {
        val next = reader.next() ?: break
        val token = when (next) {
            '\\' -> SymbolToken(parseEscapedCharacter(reader, alphabet))
            '[' -> SymbolToken(parseSetNotation(reader, alphabet))
            '|' -> UnionOperator
            '*' -> RepeatOperator(0, null)
            '+' -> RepeatOperator(1, null)
            '?' -> RepeatOperator(0, 1)
            '{' -> parseRepeatNotation(reader)
            '(' -> LeftBracket
            ')' -> RightBracket
            '.' -> SymbolToken(Symbol(alphabet)) // TODO
            else -> SymbolToken(Symbol(setOf(next)))
        }

        if (result.isNotEmpty() && (result.last() is SymbolToken || result.last() is RightBracket) && (token is SymbolToken || token is LeftBracket))
            result.add(ConcatenationOperator)
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
                    throw RuntimeException("") // TODO
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
                    throw RuntimeException("Unbalanced right bracket at ${TODO()}")
                }
            }
        }
    }

    while (operatorStack.isNotEmpty()) {
        val token = operatorStack.pop()
        if (token is LeftBracket) {
            throw RuntimeException("Unbalanced left bracket at ${TODO()}")
        } else {
            applyOperator(token)
        }
    }

    return results.pop()
}
