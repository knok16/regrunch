package com.github.knok16.regrunch.nfa

import com.github.knok16.regrunch.State
import com.github.knok16.regrunch.dfa.DFA
import com.github.knok16.regrunch.dfa.DFABuilder

fun <A, S> convertToDFA(nfa: NFA<A, S>): DFA<A, State> {
    val builder = DFABuilder(nfa.alphabet)

    val correspondence = HashMap<Set<S>, State>()
    correspondence[setOf(nfa.startState)] = builder.startState

    fun dfs(nfaStates: Set<S>, dfaState: State) {
        val symbols = nfaStates.flatMap { state -> nfa.transitionsFrom(state) }.toSet()

        for (symbol in symbols) {
            val key = nfa.transition(nfaStates, symbol)

            val to = if (key in correspondence) {
                correspondence.getValue(key)
            } else {
                val r = builder.newState()
                correspondence[key] = r
                dfs(key, r)
                r
            }
            builder.transition(dfaState, to, symbol)
        }
    }

    dfs(setOf(nfa.startState), builder.startState)

    correspondence
        .filterKeys { nfaStates -> (nfaStates intersect nfa.finalStates).isNotEmpty() }
        .values
        .forEach(builder::markAsFinal)

    return builder.build()
}

fun <A, S> NFA<A, S>.toDFA(): DFA<A, State> = convertToDFA(this)
