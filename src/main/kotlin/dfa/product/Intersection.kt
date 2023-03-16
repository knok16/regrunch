package dfa.product

import State
import dfa.DFA

fun <A, S1, S2> intersection(dfa1: DFA<A, S1>, dfa2: DFA<A, S2>): DFA<A, State> =
    buildProductDFA(dfa1, dfa2) { (s1, s2) ->
        s1 in dfa1.finalStates && s2 in dfa2.finalStates
    }
