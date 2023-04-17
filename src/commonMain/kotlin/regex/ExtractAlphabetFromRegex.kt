package regex

internal fun error(characterClassName: String) =
    IllegalArgumentException("Shorthand character class ($characterClassName) is used in regex, it is not possible to pinpoint exact symbol set that should be used")

/**
 * Function to collect exact symbols that is used in regex,
 * that can later be used as an alphabet when converting regex into epsilon NFA
 * @param regexPart regex to collect symbols from
 * @return collected symbols
 * @throws IllegalArgumentException in case shorthand classes or set notation with negation are used in regex
 */
fun extractAlphabet(regexPart: RegexPart): Set<Char> = when (regexPart) {
    is ExactSymbol -> setOf(regexPart.value)

    is AnySymbol -> throw error("any symbol")
    is DigitSymbol -> throw error("digit symbol")
    is NonDigitSymbol -> throw error("non-digit symbol")
    is WhitespaceSymbol -> throw error("whitespace symbol")
    is NonWhitespaceSymbol -> throw error("non-whitespace symbol")
    is WordSymbol -> throw error("word symbol")
    is NonWordSymbol -> throw error("non-word symbol")

    is SetNotationSymbol -> if (!regexPart.negated) regexPart.symbols.flatMap { extractAlphabet(it) }.toSet()
    else throw IllegalArgumentException("Negated character class is used in regex, it is not possible to pinpoint exact symbol set that should be used")

    is Anchor -> emptySet()
    is Concatenation -> regexPart.parts.flatMap { extractAlphabet(it) }.toSet()
    is Union -> regexPart.parts.flatMap { extractAlphabet(it) }.toSet()
    is Repeat -> extractAlphabet(regexPart.part)
}
