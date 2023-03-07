package regex

sealed interface RegexPart

data class Symbol(val options: Set<Char>) : RegexPart

data class Concatenation(val parts: List<RegexPart>) : RegexPart

data class Repeat(val part: RegexPart, val min: Int, val max: Int?) : RegexPart

data class Union(val parts: Set<RegexPart>) : RegexPart