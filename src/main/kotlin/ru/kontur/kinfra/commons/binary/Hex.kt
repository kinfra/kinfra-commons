package ru.kontur.kinfra.commons.binary

private val hexDigitsLower = "0123456789abcdef".toCharArray()
private val hexDigitsUpper = "0123456789ABCDEF".toCharArray()

/**
 * Returns this byte in form of two hexadecimal digits.
 */
fun Byte.toHexString(upperCase: Boolean = false): String {
    val unsigned = asUnsigned()
    val hexDigits = if (upperCase) hexDigitsUpper else hexDigitsLower
    val chars = CharArray(2)
    chars[0] = hexDigits[unsigned shr 4]
    chars[1] = hexDigits[unsigned and 0xF]
    return String(chars)
}

@Deprecated(message = "for compatibility", level = DeprecationLevel.HIDDEN)
fun Byte.toHexString(): String {
    return toHexString(upperCase = false)
}

/**
 * Returns hexadecimal representation of bytes in this array.
 *
 * Inverse transformation is possible with [byteArrayOfHex].
 */
fun ByteArray.toHexString(upperCase: Boolean = false): String = buildString(size * 2) {
    for (byte in this@toHexString) {
        appendHexByte(byte, upperCase)
    }
}

@Deprecated(message = "for compatibility", level = DeprecationLevel.HIDDEN)
fun ByteArray.toHexString(): String {
    return toHexString(upperCase = false)
}

/**
 * Constructs array of bytes from its hexadecimal representation.
 *
 * Inverse transformation is possible with [ByteArray.toHexString].
 */
fun byteArrayOfHex(hex: String): ByteArray {
    require(hex.length % 2 == 0) {
        "Hex string must have even number of digits (actual is ${hex.length})"
    }

    val array = ByteArray(hex.length / 2)
    for (i in array.indices) {
        val msb = Character.digit(hex[i * 2], 16) shl 4
        val lsb = Character.digit(hex[i * 2 + 1], 16)
        array[i] = (msb or lsb).toByte()
    }
    return array
}

/**
 * Append hexadecimal representation of a [byte] to this builder.
 * Exactly two hexadecimal digits are appended.
 */
fun StringBuilder.appendHexByte(byte: Byte, upperCase: Boolean = false) {
    val unsigned = byte.asUnsigned()
    val hexDigits = if (upperCase) hexDigitsUpper else hexDigitsLower
    append(hexDigits[unsigned shr 4])
    append(hexDigits[unsigned and 0xF])
}

@Deprecated(message = "for compatibility", level = DeprecationLevel.HIDDEN)
fun StringBuilder.appendHexByte(byte: Byte) {
    appendHexByte(byte, upperCase = false)
}
