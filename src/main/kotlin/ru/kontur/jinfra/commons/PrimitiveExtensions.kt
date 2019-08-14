package ru.kontur.jinfra.commons

/**
 * If this boolean is true, calls specified [block] and returns its result.
 * Otherwise just returns `null`.
 */
inline fun <R> Boolean.thenTake(block: () -> R): R? = if (this) block() else null

/**
 * If this string is not empty, returns specified [prefix] followed by this string.
 * Otherwise returns this string.
 */
fun String.prefixNotEmpty(prefix: String): String {
    return if (isNotEmpty()) prefix + this else this
}
