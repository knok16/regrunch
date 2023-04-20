package com.github.knok16.regrunch.utils

// TODO rework
actual class Stack<E> actual constructor() {
    private val list: MutableList<E> = mutableListOf()

    actual fun push(element: E): E {
        list.add(element)
        return element
    }

    actual fun pop(): E =
        list.removeAt(list.lastIndex)

    actual fun peek(): E =
        list.last()

    actual val size: Int
        get() = list.size
}