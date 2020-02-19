package ru.kontur.kinfra.commons.time

import java.time.Duration
import kotlin.time.*

/**
 * An instantaneous point on the time-line.
 *
 * Backed by [System.nanoTime].
 *
 * To be replaced by [ClockMark] of [MonoClock] after its final release.
 */
class MonotonicInstant private constructor(
    private val nanoOffset: Long
) : Comparable<MonotonicInstant> {
    // todo: deprecate after release of kotlin.time

    operator fun plus(duration: Duration): MonotonicInstant {
        return MonotonicInstant(nanoOffset + duration.toNanos())
    }

    operator fun minus(duration: Duration): MonotonicInstant {
        return MonotonicInstant(nanoOffset - duration.toNanos())
    }

    operator fun minus(other: MonotonicInstant): Duration {
        return Duration.ofNanos(nanoOffset - other.nanoOffset)
    }

    override fun compareTo(other: MonotonicInstant): Int {
        // implies that nanoOffset is monotonic
        return nanoOffset.compareTo(other.nanoOffset)
    }

    override fun equals(other: Any?): Boolean {
        return other is MonotonicInstant && nanoOffset == other.nanoOffset
    }

    override fun hashCode(): Int {
        return nanoOffset.hashCode()
    }

    override fun toString(): String {
        return "MonotonicInstant(${Duration.ofSeconds(0, nanoOffset)} since origin)"
    }

    companion object {

        private val ORIGIN_NANOS = rawNow()

        fun now(): MonotonicInstant {
            return MonotonicInstant(rawNow() - ORIGIN_NANOS)
        }

        private fun rawNow(): Long {
            return System.nanoTime()
        }

    }

}
