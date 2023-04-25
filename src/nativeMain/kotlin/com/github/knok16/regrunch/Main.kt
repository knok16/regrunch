package com.github.knok16.regrunch

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.mutuallyExclusiveOptions
import com.github.ajalt.clikt.parameters.groups.single
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.transformAll
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import com.github.knok16.regrunch.dfa.allStringsAlphabetically
import com.github.knok16.regrunch.dfa.anyStringOfLengthUpTo
import com.github.knok16.regrunch.dfa.intersect
import com.github.knok16.regrunch.dfa.isLanguageInfinite
import com.github.knok16.regrunch.epsilonnfa.toNFA
import com.github.knok16.regrunch.nfa.toDFA
import com.github.knok16.regrunch.regex.ParseException
import com.github.knok16.regrunch.regex.extractAlphabet
import com.github.knok16.regrunch.regex.parse
import com.github.knok16.regrunch.regex.toEpsilonNFA

private val asciiAlphabet = (0x00..0x7F).map { it.toChar() }.toSet()
private val printableAsciiAlphabet = (0x20..0x7E).map { it.toChar() }.toSet()

// TODO improve help strings, check grammar
class Regrunch : CliktCommand("Generate strings from regex") {
    private val regex by argument(help = "Regex to generate strings")

    private val maxLength by option(
        "-m", "--max-length",
        help = "Limits length of strings generated from regex, required in case regex defines infinite amount of strings"
    ).int().restrictTo(min = 0)

    private val alphabet by mutuallyExclusiveOptions(
        option(
            "--alphabet", "-a",
            help = "Symbols set used for string generation"
        ).choice(
            "ascii" to asciiAlphabet,
            "printable-ascii" to printableAsciiAlphabet
        ),
        option(
            "--symbols", "-s",
            help = "Symbols used for string generation"
        ).transformAll { values -> values.flatMap { it.toList() }.toSet() }
    ).single()

    override fun run() {
        val regex = try {
            parse(regex)
        } catch (e: ParseException) {
            throw PrintMessage(
                """Wrong regex pattern:
                |${e.message}
                |$regex
                |${" ".repeat(e.fromIndex) + "^".repeat(e.toIndex - e.fromIndex + 1)}
                """.trimMargin(),
                error = true
            )
        }

        val alphabet = alphabet ?: try {
            extractAlphabet(regex)
        } catch (e: IllegalArgumentException) {
            printableAsciiAlphabet
        }

        val dfa = regex.toEpsilonNFA(alphabet).toNFA().toDFA().let { dfa ->
            maxLength?.let { dfa intersect anyStringOfLengthUpTo(alphabet, it) } ?: dfa
        }

        if (isLanguageInfinite(dfa)) {
            throw PrintMessage(
                "Number of strings defined by the pattern is unbounded, you can use --max-length option to limit length of the strings",
                error = true
            )
        }

        dfa.allStringsAlphabetically().forEach {
            echo(it)
        }
    }
}

fun main(args: Array<String>) = Regrunch().main(args)
