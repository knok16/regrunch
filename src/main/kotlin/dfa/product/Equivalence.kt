package dfa.product

import dfa.DFA

fun <A, S1, S2> isLanguagesEqual(
    dfa1: DFA<A, S1>,
    dfa2: DFA<A, S2>
): Boolean {
    if (dfa1.alphabet != dfa2.alphabet) throw IllegalArgumentException("DFAs have different alphabets")

    var result = true

    product(dfa1, dfa2, { (s1, s2) ->
        // TODO can we return false right away?
        // probably we will need to inline ::product function
        if ((s1 in dfa1.finalStates) != (s2 in dfa2.finalStates)) result = false
    }, { _, _, _ -> })

    return result
}