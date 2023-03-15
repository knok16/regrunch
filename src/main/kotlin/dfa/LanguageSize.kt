package dfa

import java.math.BigInteger

fun <A, S> isLanguageEmpty(dfa: DFA<A, S>): Boolean {
    val visited = HashSet<S>()

    fun dfs(state: S): Boolean {
        if (state in dfa.finalStates) return true

        if (!visited.add(state)) return false

        return dfa.alphabet.any { symbol -> dfs(dfa.transition(state, symbol)) }
    }

    return !dfs(dfa.startState)
}

fun <A, S> languageSize(dfa: DFA<A, S>): BigInteger? {
    val deadStates = dfa.deadStates()
    val currentlyIn = HashSet<S>()
    val resultCache = HashMap<S, BigInteger?>()

    fun dfs(state: S): BigInteger? {
        if (state in deadStates) return BigInteger.ZERO

        if (state in resultCache) return resultCache[state]

        if (state in currentlyIn) return null

        currentlyIn.add(state)

        val result = dfa.alphabet.fold<A, BigInteger?>(
            if (state in dfa.finalStates) BigInteger.ONE else BigInteger.ZERO
        ) { acc, symbol ->
            acc?.let { dfs(dfa.transition(state, symbol))?.let { it + acc } }
        }
        resultCache[state] = result

        currentlyIn.remove(state)

        return result
    }

    return dfs(dfa.startState)
}

fun <A, S> isLanguageInfinite(dfa: DFA<A, S>): Boolean {
    val deadStates = dfa.deadStates()
    val currentlyIn = HashSet<S>()
    val visited = HashSet<S>()

    fun dfs(state: S): Boolean {
        if (state in deadStates) return false
        if (state in visited) return false
        if (state in currentlyIn) return true

        currentlyIn.add(state)

        val result = dfa.alphabet.any { symbol -> dfs(dfa.transition(state, symbol)) }

        currentlyIn.remove(state)
        visited.add(state)

        return result
    }

    return dfs(dfa.startState)
}