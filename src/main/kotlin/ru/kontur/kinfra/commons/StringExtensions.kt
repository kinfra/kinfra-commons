package ru.kontur.kinfra.commons

/**
 * Returns a copy of this string having its first letter titlecased using the rules of the default locale,
 * or the original string if it's empty or already starts with a title case letter.
 */
public fun String.capitalize(): String {
    return if (isEmpty() || !this[0].isLowerCase()) {
        this
    } else {
        replaceFirstChar { it.titlecase() }
    }
}
