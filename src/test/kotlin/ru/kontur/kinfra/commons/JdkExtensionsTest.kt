package ru.kontur.kinfra.commons

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

}
