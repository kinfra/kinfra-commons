package ru.kontur.kinfra.commons

import java.util.*

/**
 * Returns name of this enum constant in lower case.
 */
val Enum<*>.lowerCaseName: String
    get() = name.toLowerCase(Locale.ROOT)
