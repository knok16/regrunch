package utils

expect class Queue<E>() {
    val size: Int
    fun offer(element: E): Boolean
    fun remove(): E
}

fun <T> Queue<T>.isNotEmpty(): Boolean = size > 0