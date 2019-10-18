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
    private val nanos: Long
) : Comparable<MonotonicInstant> {
    // todo: deprecate after release of kotlin.time

    operator fun plus(duration: Duration): MonotonicInstant {
        return MonotonicInstant(nanos + duration.toNanos())
    }

    operator fun minus(duration: Duration): MonotonicInstant {
        return MonotonicInstant(nanos - duration.toNanos())
    }

    operator fun minus(other: MonotonicInstant): Duration {
        return Duration.ofNanos(nanos - other.nanos)
    }

    override fun compareTo(other: MonotonicInstant): Int {
        return (nanos - other.nanos).compareTo(0)
    }

    override fun equals(other: Any?): Boolean {
        return other is MonotonicInstant && nanos == other.nanos
    }

    override fun hashCode(): Int {
        return nanos.hashCode()
    }

    companion object {

        fun now(): MonotonicInstant {
            return MonotonicInstant(System.nanoTime())
        }

    }

}
