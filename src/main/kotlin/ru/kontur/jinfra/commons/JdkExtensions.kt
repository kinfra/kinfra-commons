package ru.kontur.jinfra.commons

import ru.kontur.jinfra.commons.time.TimeTicks
import java.time.Instant
import java.util.*

/**
 * Return the value if present, otherwise return `null`.
 *
 * This method is safer alternative to `orElse(null)`, whose return type is `T!`.
 */
fun <T> Optional<T>.unwrap(): T? = orElse(null)

/**
 * Return timestamp of this time-based (version 1) UUID as an [Instant].
 *
 * @throws UnsupportedOperationException if this UUID is not time-based
 * @see UUID.timestamp
 */
fun UUID.instant(): Instant {
    return TimeTicks.UuidTimestamp(timestamp()).toInstant()
}
