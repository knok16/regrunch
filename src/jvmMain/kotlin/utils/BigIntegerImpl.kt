package utils

actual typealias BigInteger = java.math.BigInteger

actual fun add(a: BigInteger, b: BigInteger): BigInteger = a.add(b)

actual fun String.toBigInteger(): BigInteger = BigInteger(this)

actual fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)

actual val ZERO: BigInteger = java.math.BigInteger.ZERO

actual val ONE: BigInteger = java.math.BigInteger.ONE
