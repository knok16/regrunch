package dfa

class DFA<A>(
    private val startState: State,
    private val finalStates: Set<State>,
    private val transitions: List<Map<A, State>>
) {
    fun accept(str: Sequence<A>): Boolean {
        var state = startState
        for (c in str) {
            state = transitions[state][c] ?: return false
        }
        return state in finalStates
    }
}

fun <A> DFA<A>.accept(str: List<A>): Boolean = accept(str.asSequence())

fun DFA<Char>.accept(str: String): Boolean = accept(str.asSequence())
