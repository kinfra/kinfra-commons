package ru.kontur.kinfra.commons.time

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.GenericComparableAssert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.kontur.kinfra.commons.time.TimeTicks.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*

class TimeTicksTest {

    private val farPast = LocalDate.of(1, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
    private val farFuture = LocalDate.of(2999, 12, 31).atStartOfDay().toInstant(ZoneOffset.UTC)

    private val sampleTickPrecisionInstant = Instant.parse("2019-08-15T04:42:45.1885735Z")
    private val sampleUuid = UUID.fromString("1fe6d6a7-bf17-11e9-ad24-2c4d54d05ed4")

    @Test
    fun time_span_not_equals_timestamp() {
        val timeSpan = TimeSpan(123)
        val timestamp = EpochTimestamp(123)

        assertNotEquals(timeSpan, timestamp)
        assertNotEquals(timestamp, timeSpan)
    }

    @Nested
    inner class TimeSpanTest {

        @Test
        fun round_trip_zero_duration() {
            val input = Duration.ZERO
            val output = input.toTicks().toDuration()
            assertEquals(input, output)
        }

        @Test
        fun round_trip_sample_duration() {
            val input = Duration.ofDays(366).plusNanos(700)
            val output = input.toTicks().toDuration()
            assertEquals(input, output)
        }

        @Test
        fun round_trip_big_duration() {
            val input = Duration.between(farPast, farFuture)
            val output = input.toTicks().toDuration()
            assertEquals(input, output)
        }

        @Test
        fun compare_equal() {
            val result = TimeSpan(1).compareTo(TimeSpan(1))
            assertEquals(0, result)
        }

        @Test
        fun compare_not_equal() {
            val result = TimeSpan(1).compareTo(TimeSpan(2))
            assertThat(result).isLessThan(0)
        }

        @Test
        fun equals() {
            assertEquals(TimeSpan(1), TimeSpan(1))
        }

        @Test
        fun to_duration_sample() {
            val input = TimeSpan(10_020_034)
            val expected = Duration.ofSeconds(1)
                .plusMillis(2)
                .plus(3, ChronoUnit.MICROS)
                .plusNanos(400)
            val result = input.toDuration()
            assertEquals(expected, result)
        }

    }

    @Nested
    inner class TimestampTest {

        @Test
        fun compared_by_absolute_time() {
            val earlier = DotNetTimestamp(2)
            val later = UuidTimestamp(1)
            GenericComparableAssert<Timestamp>(earlier).isLessThan(later)


            val sampleInstant = sampleTickPrecisionInstant
            GenericComparableAssert<Timestamp>(sampleInstant.toDotNetTime())
                .isEqualByComparingTo(sampleInstant.toEpochTicks())
        }

        @Test
        fun equals_by_absolute_time() {
            val dotnet = DotNetTimestamp(1)
            val uuid = UuidTimestamp(1)
            assertNotEquals(dotnet, uuid)

            val sampleInstant = sampleTickPrecisionInstant
            assertEquals(sampleInstant.toDotNetTime(), sampleInstant.toEpochTicks())
        }

    }

    @Nested
    inner class DotNetTimeTest {

        @Test
        fun round_trip_far_past() = roundTrip(farPast)

        @Test
        fun round_trip_far_future() = roundTrip(farFuture)

        @Test
        fun round_trip_sample() = roundTrip(sampleTickPrecisionInstant)

        private fun roundTrip(instant: Instant) {
            assertEquals(instant, instant.toDotNetTime().toInstant())
        }

        @Test
        fun from_instant_sample() {
            val input = sampleTickPrecisionInstant
            val expected = DotNetTimestamp(637014409651885735L)
            val result = input.toDotNetTime()
            assertEquals(expected, result)
        }

    }

    @Nested
    inner class UuidTimeTest {

        @Test
        fun round_trip_sample() {
            val input = sampleTickPrecisionInstant
            val output = input.toUuidTime().toInstant()
            assertEquals(input, output)
        }

        @Test
        fun from_instant_sample() {
            val input = sampleTickPrecisionInstant
            val expected = UuidTimestamp(sampleUuid.timestamp())
            val result = input.toUuidTime()
            assertEquals(expected, result)
        }

    }

    @Nested
    inner class EpochTimeTest {

        @Test
        fun round_trip_sample() {
            val input = sampleTickPrecisionInstant
            val output = input.toEpochTicks().toInstant()
            assertEquals(input, output)
        }

        @Test
        fun from_instant_sample() {
            val input = Instant.parse("2018-05-30T11:32:00Z")
            val expected = EpochTimestamp(15_276_799_200_000_000L)
            val result = input.toUuidTime()
            assertEquals(expected, result)
        }

    }

}
