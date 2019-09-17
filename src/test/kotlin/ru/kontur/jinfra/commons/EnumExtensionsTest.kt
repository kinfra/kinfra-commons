package ru.kontur.jinfra.commons

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class EnumExtensionsTest {

    @Nested
    inner class LowerCaseName {

        @Test
        fun simple() {
            assertEquals("simple_constant", TestEnum.SIMPLE_CONSTANT.lowerCaseName)
        }

        @Test
        fun russian() {
            assertEquals("имя_на_русском", TestEnum.ИМЯ_НА_РУССКОМ.lowerCaseName)
        }

        @Test
        fun turkish_locale() {
            val locale = Locale.getDefault()
            try {
                // I -> ı in turkish locale
                Locale.setDefault(Locale.forLanguageTag("tr"))

                assertEquals("simple_constant", TestEnum.SIMPLE_CONSTANT.lowerCaseName)
            } finally {
                Locale.setDefault(locale)
            }
        }

    }

    enum class TestEnum {
        SIMPLE_CONSTANT,
        @Suppress("EnumEntryName", "NonAsciiCharacters")
        ИМЯ_НА_РУССКОМ,
    }

}
