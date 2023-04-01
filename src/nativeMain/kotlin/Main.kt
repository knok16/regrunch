import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import dfa.allStringsAlphabetically
import epsilonnfa.toNFA
import nfa.toDFA
import regex.ParseException
import regex.parse
import regex.toEpsilonNFA

class Regrunch : CliktCommand("Generate strings from regex") {
    private val regex by argument(help = "Regex to generate strings")

    override fun run() {
        try {
            val regex = parse(regex)

            val asciiAlphabet = (0..127).map { it.toChar() }.toSet()

            regex.toEpsilonNFA(asciiAlphabet).toNFA().toDFA().allStringsAlphabetically().forEach {
                echo(it)
            }
        } catch (e: ParseException) {
            echo("Wrong regex pattern:", err = true)
            echo(e.message, err = true)
            echo(regex, err = true)
            echo(" ".repeat(e.fromIndex) + "^".repeat(e.toIndex - e.fromIndex + 1), err = true)
            throw ProgramResult(1)
        }
    }
}

fun main(args: Array<String>) = Regrunch().main(args)
