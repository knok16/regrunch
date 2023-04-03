import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import dfa.allStringsAlphabetically
import dfa.anyStringOfLengthUpTo
import dfa.intersect
import dfa.isLanguageInfinite
import epsilonnfa.toNFA
import nfa.toDFA
import regex.ParseException
import regex.parse
import regex.toEpsilonNFA

private val asciiAlphabet = (0..127).map { it.toChar() }.toSet()

class Regrunch : CliktCommand("Generate strings from regex") {
    private val regex by argument(help = "Regex to generate strings")
    private val maxLength by option(
        "-m", "--max-length",
        help = "Limits length of strings generated from regex, required in case regex defines infinite amount of strings"
    ).int().restrictTo(min = 0)

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

        val dfa = regex.toEpsilonNFA(asciiAlphabet).toNFA().toDFA().let { dfa ->
            maxLength?.let { dfa intersect anyStringOfLengthUpTo(asciiAlphabet, it) } ?: dfa
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
