package ru.kontur.jinfra.commons.time

import java.time.*
import java.util.concurrent.TimeUnit

/**
 * Represents [instant][Instant] or [duration][Duration] in form of 100-nanosecond time intervals.
 *
 * @property value count of ticks (100-nanosecond intervals)
 */
sealed class TimeTicks(val value: Long) {

    /**
     * Amount of time.
     */
    class TimeSpan(ticks: Long) : TimeTicks(ticks), Comparable<TimeSpan> {

        fun toDuration(): Duration {
            val seconds = secondsFromTicks(value)
            val nanos = nanosFromTicks(value)
            return Duration.ofSeconds(seconds, nanos)
        }

        override fun compareTo(other: TimeSpan): Int {
            return value.compareTo(other.value)
        }

        override fun equals(other: Any?): Boolean {
            return other is TimeSpan && other.value == this.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString() = "$value ticks (${toDuration()})"

    }

    /**
     * Point on the time-line.
     */
    abstract class Timestamp internal constructor(ticks: Long) : TimeTicks(ticks), Comparable<Timestamp> {

        /**
         * Count of ticks between the reference time (at the zeroth tick) and Java epoch.
         */
        protected abstract val epochOffset: Long

        private val epochTicks: Long
            get() = Math.addExact(value, epochOffset)

        fun toInstant(): Instant {
            val seconds = secondsFromTicks(epochTicks)
            val nanos = nanosFromTicks(epochTicks)
            return Instant.ofEpochSecond(seconds, nanos)
        }

        final override fun compareTo(other: Timestamp): Int {
            return epochTicks.compareTo(other.epochTicks)
        }

        final override fun equals(other: Any?): Boolean {
            return other is Timestamp
                && other.epochTicks == this.epochTicks
        }

        final override fun hashCode(): Int {
            return epochTicks.hashCode()
        }

        final override fun toString() = "$value ticks (${toInstant()})"

    }

    /**
     * Point on the time-line in .NET format.
     *
     * Value represents count of 100-nanosecond intervals since midnight, January 1, 1 UTC.
     *
     * See more at [https://msdn.microsoft.com/en-us/library/system.datetime.ticks.aspx].
     */
    class DotNetTimestamp(ticks: Long) : Timestamp(ticks) {

        override val epochOffset: Long get() = DOT_NET_EPOCH_OFFSET

    }

    /**
     * Point on the time-line in format used by time-based (version 1) UUIDs.
     *
     * Value represents count of 100-nanosecond intervals since midnight, October 15, 1582 UTC.
     */
    class UuidTimestamp(ticks: Long) : Timestamp(ticks) {

        override val epochOffset: Long get() = UUID_EPOCH_OFFSET

    }

    /**
     * Point on the time-line in format used by e.g. Vostok Hercules.
     *
     * Value represents count of 100-nanosecond intervals since Java epoch of 1970-01-01T00:00:00Z.
     */
    class EpochTimestamp(ticks: Long) : Timestamp(ticks) {

        override val epochOffset: Long get() = 0

    }

}

fun Instant.toDotNetTime(): TimeTicks.DotNetTimestamp {
    return TimeTicks.DotNetTimestamp(toTicks(DOT_NET_EPOCH_OFFSET))
}

fun Instant.toUuidTime(): TimeTicks.UuidTimestamp {
    return TimeTicks.UuidTimestamp(toTicks(UUID_EPOCH_OFFSET))
}

fun Instant.toEpochTicks(): TimeTicks.EpochTimestamp {
    return TimeTicks.EpochTimestamp(toTicks(0))
}

private fun Instant.toTicks(epochOffset: Long): Long {
    val epochTicks = ticksFromSeconds(epochSecond, nano)
    return Math.subtractExact(epochTicks, epochOffset)
}

fun Duration.toTicks(): TimeTicks.TimeSpan {
    val ticks = ticksFromSeconds(seconds, nano)
    return TimeTicks.TimeSpan(ticks)
}

private fun ticksFromSeconds(seconds: Long, nanos: Int): Long {
    return Math.multiplyExact(seconds, TICKS_PER_SECOND) + nanos / NANOS_PER_TICK
}

private fun secondsFromTicks(ticks: Long): Long {
    return ticks / TICKS_PER_SECOND
}

private fun nanosFromTicks(ticks: Long): Long {
    return Math.multiplyExact(ticks % TICKS_PER_SECOND, NANOS_PER_TICK)
}

private const val NANOS_PER_TICK = 100L
private val NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1)
private val TICKS_PER_SECOND = NANOS_PER_SECOND / NANOS_PER_TICK

private val DOT_NET_EPOCH_OFFSET = getEpochTicksOffset(LocalDate.of(1, 1, 1))
private val UUID_EPOCH_OFFSET = getEpochTicksOffset(LocalDate.of(1582, 10, 15))

private fun getEpochTicksOffset(reference: LocalDate): Long {
    val instant = LocalDateTime.of(reference, LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC)
    return instant.toTicks(0)
}
