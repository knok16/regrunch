package regex

sealed interface RegexPart

data class Concatenation(val parts: List<RegexPart>) : RegexPart

data class Repeat(val part: RegexPart, val min: Int, val max: Int?) : RegexPart

data class Union(val parts: Set<RegexPart>) : RegexPart

sealed interface Symbol : RegexPart

@JvmInline
value class ExactSymbol(val value: Char) : Symbol

object AnySymbol : Symbol
object DigitSymbol : Symbol
object NonDigitSymbol : Symbol
object WhitespaceSymbol : Symbol
object NonWhitespaceSymbol : Symbol
object WordSymbol : Symbol
object NonWordSymbol : Symbol
data class SetNotationSymbol(val symbols: Set<Symbol>, val negated: Boolean = false) : Symbol
object BeginningOfLine : Symbol
object EndOfLine : Symbol
