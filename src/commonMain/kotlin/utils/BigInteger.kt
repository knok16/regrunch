package utils

expect class BigInteger

expect fun add(a: BigInteger, b: BigInteger): BigInteger

expect fun String.toBigInteger(): BigInteger

expect fun Long.toBigInteger(): BigInteger

infix operator fun BigInteger.plus(another: BigInteger): BigInteger = add(this, another)

expect val ZERO: BigInteger

expect val ONE: BigInteger
