package utils

// TODO rework
actual class Queue<E> actual constructor() {
    private val list: MutableList<E> = mutableListOf()

    actual fun offer(element: E): Boolean {
        list.add(element)
        return true
    }

    actual fun remove(): E =
        list.removeAt(0)

    actual val size: Int
        get() = list.size
}