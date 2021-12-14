package ru.kontur.kinfra.commons

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EncodingTest {

    @Nested
    inner class EnumTest {

        val sampleEncoding = Encoding.enum { it: SampleEnum -> it.value }

        @Test
        fun `encode sample value`() {
            val result = sampleEncoding.encode(SampleEnum.FOO)
            assertThat(result).isEqualTo(1)
        }

        @Test
        fun `decode sample value`() {
            val result = sampleEncoding.decode(1)
            assertThat(result).isEqualTo(SampleEnum.FOO)
        }

        @Test
        fun `decode unknown value`() {
            assertThrows<IllegalArgumentException> {
                sampleEncoding.decode(3)
            }
        }

        @Test
        fun `duplicate mapping`() {
            assertThrows<IllegalStateException> {
                Encoding.enum { it: SampleEnum ->
                    when (it) {
                        SampleEnum.FOO -> "foo"
                        SampleEnum.BAR -> "foo"
                    }
                }
            }
        }

    }

    @Nested
    inner class EnumBijectionTest {

        val properEncoding = Encoding.enumBijectionByName<SampleEnum, AnotherEnum>()

        @Test
        fun `encode sample value`() {
            val result = properEncoding.encode(SampleEnum.FOO)
            assertThat(result).isEqualTo(AnotherEnum.FOO)
        }

        @Test
        fun `decode sample value`() {
            val result = properEncoding.decode(AnotherEnum.FOO)
            assertThat(result).isEqualTo(SampleEnum.FOO)
        }

        @Test
        fun `non-exhaustive mapping`() {
            assertThrows<IllegalStateException> {
                Encoding.enumBijectionByName<SampleEnum, ExtendedEnum>()
            }
        }

        @Test
        fun `duplicate mapping`() {
            assertThrows<IllegalStateException> {
                Encoding.enumBijection { it: ExtendedEnum ->
                    when (it) {
                        ExtendedEnum.FOO -> SampleEnum.FOO
                        ExtendedEnum.BAR -> SampleEnum.BAR
                        ExtendedEnum.BAZ -> SampleEnum.BAR
                    }
                }
            }
        }

    }

    enum class SampleEnum(val value: Int) {
        FOO(1),
        BAR(2),
    }

    enum class AnotherEnum {
        FOO,
        BAR,
    }

    enum class ExtendedEnum {
        FOO,
        BAR,
        BAZ,
    }

}
