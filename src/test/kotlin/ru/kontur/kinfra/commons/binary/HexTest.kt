package ru.kontur.kinfra.commons.binary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class HexTest {

    private val samples = mapOf(
        0x00 to "00",
        0x0f to "0f",
        0x7f to "7f",
        0x80 to "80",
        0xf0 to "f0",
        0xff to "ff"
    )

    @Test
    fun appendHexByte_samples() {
        val input = samples.keys.map { it.toByte() }.toByteArray()
        val expected = samples.values.joinToString(separator = "")

        val output = buildString {
            for (byte in input) {
                appendHexByte(byte)
            }
        }

        assertEquals(expected, output)
    }

    @Test
    fun byte_toHexString_samples() {
        assertAll(
            samples.map { (input, expected) ->
                fun(): Unit {
                    val result = input.toByte().toHexString()
                    assertEquals(expected, result)
                }
            }
        )
    }

    @Test
    fun byteArray_toHexString_samples() {
        val input = samples.keys.map { it.toByte() }.toByteArray()
        val expected = samples.values.joinToString(separator = "")

        val output = input.toHexString()

        assertEquals(expected, output)
    }

}
