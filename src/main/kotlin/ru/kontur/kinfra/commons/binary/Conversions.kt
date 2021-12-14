package ru.kontur.kinfra.commons.binary

/**
 * Returns numeric value of this byte treating it as unsigned.
 */
public fun Byte.asUnsigned(): Int {
    return toInt() and 0xff
}

/**
 * Returns numeric value of this short treating it as unsigned.
 */
public fun Short.asUnsigned(): Int {
    return toInt() and 0xffff
}
