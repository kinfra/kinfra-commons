package ru.kontur.kinfra.commons

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PrimitiveExtensionsTest {

    @Nested
    inner class BooleanThenTake {

        @Test
        fun false_results_null() {
            val result = false.thenTake { Unit }
            assertNull(result)
        }

        @Test
        fun false_not_evaluated() {
            false.thenTake { fail<Any>() }
        }

        @Test
        fun true_results_value() {
            val result = true.thenTake { Unit }
            assertSame(Unit, result)
        }

    }

    @Nested
    inner class StringPrefixNotEmpty {

        @Test
        fun empty_results_empty() {
            val result = "".prefixNotEmpty("foo")
            assertEquals("", result)
        }

        @Test
        fun not_empty_prefixed() {
            val result = "bar".prefixNotEmpty("foo")
            assertEquals("foobar", result)
        }

    }

}
