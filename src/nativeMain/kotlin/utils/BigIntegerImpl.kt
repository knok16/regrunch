package utils

// TODO use something more appropriate (rather than Long)
actual typealias BigInteger = Long

actual fun add(a: BigInteger, b: BigInteger): BigInteger = a + b

actual fun String.toBigInteger(): BigInteger = toLong()

actual fun Long.toBigInteger(): BigInteger = this

actual val ZERO: BigInteger = 0L

actual val ONE: BigInteger = 1L
