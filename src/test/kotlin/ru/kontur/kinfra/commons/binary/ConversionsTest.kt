package ru.kontur.kinfra.commons.binary

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class ConversionsTest {

    private val byteSamples = listOf(0x0, 0x7f, 0x80, 0xff)

    private val shortSamples = listOf(0x0, 0x7fff, 0x8000, 0xffff)

    @Test
    fun byte_as_unsigned_samples() {
        assertAll(
            byteSamples.map { int ->
                fun(): Unit {
                    val result = int.toByte().asUnsigned()
                    Assertions.assertEquals(int, result)
                }
            }
        )
    }

    @Test
    fun short_as_unsigned_samples() {
        assertAll(
            shortSamples.map { int ->
                fun(): Unit {
                    val result = int.toShort().asUnsigned()
                    Assertions.assertEquals(int, result)
                }
            }
        )
    }

}
