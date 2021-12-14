package ru.kontur.kinfra.commons

/**
 * Returns name of this enum constant in lower case.
 */
public val Enum<*>.lowerCaseName: String
    get() = name.lowercase()
