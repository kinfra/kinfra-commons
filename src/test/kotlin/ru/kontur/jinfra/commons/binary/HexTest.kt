package ru.kontur.jinfra.commons.binary

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
        assertAll(
            samples.map { (input, expected) ->
                fun(): Unit {
                    val result = buildString {
                        appendHexByte(input.toByte())
                    }
                    assertEquals(expected, result)
                }
            }
        )
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

}
