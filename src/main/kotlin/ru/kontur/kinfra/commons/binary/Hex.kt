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
 *
 * Inverse transformation is possible with [byteArrayOfHex].
 */
fun ByteArray.toHexString(): String = buildString(size * 2) {
    for (byte in this@toHexString) {
        appendHexByte(byte)
    }
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
 * Exactly two hexadecimal digits in lower case are appended.
 */
fun StringBuilder.appendHexByte(byte: Byte) {
    val unsigned = byte.asUnsigned()
    append(hexDigits[unsigned shr 4])
    append(hexDigits[unsigned and 0xF])
}
