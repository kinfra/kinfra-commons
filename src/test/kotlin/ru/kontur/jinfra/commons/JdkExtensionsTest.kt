package ru.kontur.jinfra.commons

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class JdkExtensionsTest {

    @Nested
    inner class OptionalUnwrapTest {

        @Test
        fun unwrap_empty() {
            val input = Optional.empty<Any>()
            val output = input.unwrap()
            assertNull(output)
        }

        @Test
        fun unwrap_value() {
            val input = Optional.of(Unit)
            val output = input.unwrap()
            assertSame(Unit, output)
        }

    }

    @Nested
    inner class UuidInstantTest {

        @Test
        fun sample() {
            val input = UUID.fromString("1fe6d6a7-bf17-11e9-ad24-2c4d54d05ed4")
            val expected = Instant.parse("2019-08-15T04:42:45.1885735Z")
            val output = input.instant()
            assertEquals(expected, output)
        }

        @Test
        fun non_time_based() {
            assertThrows<UnsupportedOperationException> {
                UUID.randomUUID().instant()
            }
        }

    }

}
