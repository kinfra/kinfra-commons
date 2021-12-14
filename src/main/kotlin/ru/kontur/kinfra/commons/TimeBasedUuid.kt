package ru.kontur.kinfra.commons

import ru.kontur.kinfra.commons.time.TimeTicks
import ru.kontur.kinfra.commons.time.toTicks
import ru.kontur.kinfra.commons.time.toUuidTime
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

/**
 * Determines if this UUID is time-based (version 1)
 */
public val UUID.isTimeBased: Boolean
    get() = version() == 1

/**
 * Return timestamp of this time-based (version 1) UUID as an [Instant].
 *
 * @throws UnsupportedOperationException if this UUID is not time-based
 * @see UUID.timestamp
 */
public fun UUID.instant(): Instant {
    return TimeTicks.UuidTimestamp(timestamp()).toInstant()
}

/**
 * Creates a new time-based (version 1) UUID.
 */
public fun generateTimeBasedUuid(): UUID {
    return TimeBasedUuidGenerator.generate()
}

internal object TimeBasedUuidGenerator {
    // See https://www.ietf.org/rfc/rfc4122.html

    private val random = Random.Default
    private val timeSource = Clock.systemUTC()
    private val timestampAccuracy = Duration.ofMillis(1).toTicks().value

    private val lastTimestamp = AtomicLong()
    private val lsb = AtomicLong(generateLsb())

    fun generate(): UUID {
        val timestamp: Long
        var currentLsb: Long
        while (true) {
            val last = lastTimestamp.get()
            val now = timeSource.instant().toUuidTime().value
            // Validity of this value is checked by CAS on lastTimestamp
            currentLsb = lsb.get()
            if (now > last) {
                if (lastTimestamp.compareAndSet(last, now)) {
                    timestamp = now
                    break
                }
            } else if (now + timestampAccuracy > last) {
                val next = last + 1
                // Use a next timestamp value as long as it does not go far from the current time
                if (now + timestampAccuracy > next) {
                    if (lastTimestamp.compareAndSet(last, next)) {
                        timestamp = next
                        break
                    }
                } else {
                    // Wait for a next timestamp from system clock
                    Thread.yield()
                }
            } else {
                // Last timestamp is much greater than current time
                // Hence the clock jumped backwards
                // Generate a new clock sequence and node to safely reuse the same timestamps
                val newLsb = generateLsb()
                if (lsb.compareAndSet(currentLsb, newLsb)) {
                    currentLsb = newLsb
                    if (lastTimestamp.compareAndSet(last, now)) {
                        timestamp = now
                        break
                    }
                }
            }
        }
        return UUID(makeMsb(timestamp), currentLsb)
    }

    fun makeMsb(timestamp: Long): Long {
        require(timestamp ushr 60 == 0L) { "Invalid timestamp: $timestamp" }

        val hiMask = makeMask(12) shl 48
        val midMask = makeMask(16) shl 32
        val lowMask = makeMask(32)
        return timestamp.and(lowMask).shl(32) or
            timestamp.and(midMask).ushr(16) or
            timestamp.and(hiMask).ushr(48) or
            (1 shl 12)
    }

    private fun generateLsb(): Long {
        val variant = 1L shl 63
        val clock = (random.nextInt() and 0x3fff).toLong() shl 48
        // Random node is used, as described in section 4.5
        val node = run {
            val randomData = random.nextLong()
            val macMask = makeMask(48)
            val multicastBit = 1L shl 40
            multicastBit or (randomData and macMask)
        }
        return variant or clock or node
    }

    private fun makeMask(bitSize: Int): Long {
        return (1L shl bitSize) - 1
    }

}
