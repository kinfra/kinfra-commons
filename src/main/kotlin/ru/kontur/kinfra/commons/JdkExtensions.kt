package ru.kontur.kinfra.commons

import java.time.Instant
import java.util.*

/**
 * Return the value if present, otherwise return `null`.
 *
 * This method is safer alternative to `orElse(null)`, whose return type is `T!`.
 */
fun <T> Optional<T>.unwrap(): T? = orElse(null)

@Deprecated(message = "for compatibility", level = DeprecationLevel.HIDDEN)
@JvmName("instant")
fun UUID.instantCompatibilityBridge(): Instant {
    return instant()
}
