package regex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExtractAlphabetFromRegexKtTest {
    @Test
    fun extractAlphabet() {
        assertEquals(
            setOf('a', 'b', 'd', 'e', 'f'),
            extractAlphabet(
                Concatenation(
                    listOf(
                        StartOfLine,
                        ExactSymbol('a'),
                        Union(
                            setOf(
                                Repeat(ExactSymbol('d'), 2, 5),
                                SetNotationSymbol(setOf(ExactSymbol('e'), ExactSymbol('f'), ExactSymbol('a')))
                            )
                        ),
                        ExactSymbol('b'),
                        ExactSymbol('a'),
                        EndOfLine
                    )
                )
            )
        )
    }

    @Test
    fun negatedSetsAreNotSupported() {
        assertEquals(
            "Negated character class is used in regex, it is not possible to pinpoint exact symbol set that should be used",
            assertFailsWith<IllegalArgumentException> {
                extractAlphabet(
                    Concatenation(
                        listOf(
                            StartOfLine,
                            ExactSymbol('a'),
                            Union(
                                setOf(
                                    Repeat(ExactSymbol('d'), 2, 5),
                                    SetNotationSymbol(setOf(ExactSymbol('e'), ExactSymbol('a')), negated = true)
                                )
                            ),
                            ExactSymbol('b'),
                            ExactSymbol('a'),
                            EndOfLine
                        )
                    )
                )
            }.message
        )
    }

    @Test
    fun shorthandCharacterClassesAreNotSupported() {
        assertEquals(
            "Shorthand character class (digit symbol) is used in regex, it is not possible to pinpoint exact symbol set that should be used",
            assertFailsWith<IllegalArgumentException> {
                extractAlphabet(
                    Concatenation(
                        listOf(
                            StartOfLine,
                            ExactSymbol('a'),
                            DigitSymbol,
                            Union(
                                setOf(
                                    Repeat(ExactSymbol('d'), 2, 5),
                                    SetNotationSymbol(setOf(ExactSymbol('e'), ExactSymbol('a')))
                                )
                            ),
                            ExactSymbol('b'),
                            ExactSymbol('a'),
                            EndOfLine
                        )
                    )
                )
            }.message
        )
    }
}