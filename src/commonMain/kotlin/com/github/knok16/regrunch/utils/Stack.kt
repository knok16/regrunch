package com.github.knok16.regrunch.utils

expect class Stack<E>() {
    val size: Int
    fun push(element: E): E
    fun pop(): E
    fun peek(): E
}

fun <T> Stack<T>.isNotEmpty(): Boolean = size > 0
