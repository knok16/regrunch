package com.github.knok16.regrunch.regex

private fun <T1, T2> Pair<T1, T1>.mapValues(transform: (T1) -> T2): Pair<T2, T2> = transform(first) to transform(second)

internal fun Char.isWordChar(): Boolean = category in setOf(
    CharCategory.LOWERCASE_LETTER,
    CharCategory.UPPERCASE_LETTER,
    CharCategory.TITLECASE_LETTER,
    CharCategory.OTHER_LETTER,
    CharCategory.MODIFIER_LETTER,
    CharCategory.NON_SPACING_MARK,
    CharCategory.DECIMAL_DIGIT_NUMBER,
    CharCategory.CONNECTOR_PUNCTUATION
)

private fun IntRange.toAlphabet(): Set<Char> = map { it.toChar() }.toSet()

class AlphabetContext(val alphabet: Set<Char>) {
    companion object {
        val ASCII = AlphabetContext((0x00..0x7F).toAlphabet())
        val PRINTABLE_ASCII = AlphabetContext((0x20..0x7E).toAlphabet())
    }

    private val anySymbol: Set<Char> by lazy {
        alphabet - setOf(
            '\n',           // Line Feed
            '\r',           // Carriage Return
            0x85.toChar(),  // Next Line
            0x2028.toChar(),// Line Separator
            0x2029.toChar() // Paragraph Separator
        )
    }

    private val digitsPartition by lazy {
        alphabet.partition { it.isDigit() }.mapValues { it.toSet() }
    }

    private val whitespacesPartition by lazy {
        alphabet.partition { it.isWhitespace() }.mapValues { it.toSet() }
    }

    private val verticalWhitespaces: Set<Char> by lazy {
        setOf(
            '\n',           // Line Feed
            0x0B.toChar(),  // Vertical Tab
            0x0C.toChar(),  // Form Feed
            '\r',           // Carriage Return
            0x85.toChar(),  // Next Line
            0x2028.toChar(),// Line Separator
            0x2029.toChar() // Paragraph Separator
        ).filter { it in alphabet }.toSet()
    }

    private val horizontalWhitespaces: Set<Char> by lazy {
        alphabet.filter { it == '\t' || it.category == CharCategory.SPACE_SEPARATOR }.toSet()
    }

    private val wordCharactersPartition by lazy {
        alphabet.partition { it.isWordChar() }.mapValues { it.toSet() }
    }

    fun convertToChars(symbol: Symbol): Set<Char> = when (symbol) {
        is ExactSymbol -> if (symbol.value in alphabet) setOf(symbol.value) else emptySet()
        is AnySymbol -> anySymbol
        is DigitSymbol -> digitsPartition.first
        is NonDigitSymbol -> digitsPartition.second
        is WhitespaceSymbol -> whitespacesPartition.first
        is NonWhitespaceSymbol -> whitespacesPartition.second
        is VerticalWhitespaceSymbol -> verticalWhitespaces
        is HorizontalWhitespaceSymbol -> horizontalWhitespaces
        is WordSymbol -> wordCharactersPartition.first
        is NonWordSymbol -> wordCharactersPartition.second
        is SetNotationSymbol -> {
            val symbols = symbol.symbols.flatMap { convertToChars(it) }.toSet()
            if (symbol.negated) alphabet - symbols else symbols
        }

        is Anchor -> throw IllegalArgumentException("Anchors are not supported")
    }
}
