package ru.kontur.kinfra.commons.binary

private val hexDigits = "0123456789abcdef".toCharArray()

/**
 * Returns this byte in form of two hexadecimal digits.
 */
fun Byte.toHexString(): String {
    val unsigned = asUnsigned()
    val chars = CharArray(2)
    chars[0] = hexDigits[unsigned shr 4]
    chars[1] = hexDigits[unsigned and 0xF]
    return String(chars)
}

/**
 * Returns hexadecimal representation of bytes in this array.
 */
fun ByteArray.toHexString(): String = buildString(size * 2) {
    for (byte in this@toHexString) {
        appendHexByte(byte)
    }
}

/**
 * Append hexadecimal representation of a [byte] to this builder.
 * Exactly two hexadecimal digits in lower case are appended.
 */
fun StringBuilder.appendHexByte(byte: Byte) {
    val unsigned = byte.asUnsigned()
    append(hexDigits[unsigned shr 4])
    append(hexDigits[unsigned and 0xF])
}
