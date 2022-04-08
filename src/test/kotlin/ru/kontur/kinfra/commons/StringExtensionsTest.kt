package ru.kontur.kinfra.commons

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StringExtensionsTest {

    @Nested
    inner class Capitalize {

        @Test
        fun empty() {
            assertThat("".capitalize()).isEqualTo("")
        }

        @Test
        fun lowercased() {
            assertThat("foo".capitalize()).isEqualTo("Foo")
        }

        @Test
        fun uppercased() {
            val input = "Foo"
            assertThat(input.capitalize()).isSameAs(input)
        }

    }

}
