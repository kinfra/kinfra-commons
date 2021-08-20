package ru.kontur.kinfra.commons

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTimeout
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors

class TimeBasedUuidTest {

    private val sampleTimeBasedUuid = UUID.fromString("1fe6d6a7-bf17-11e9-ad24-2c4d54d05ed4")

    @Nested
    inner class IsTimeBased {

        @Test
        fun time_based_sample() {
            assertThat(sampleTimeBasedUuid).matches { it.isTimeBased }
        }

        @Test
        fun random_sample() {
            assertThat(UUID.randomUUID()).matches { !it.isTimeBased }
        }

        @Test
        fun name_based_sample() {
            assertThat(UUID.nameUUIDFromBytes(byteArrayOf())).matches { !it.isTimeBased }
        }

    }

    @Nested
    inner class InstantTest {

        @Test
        fun sample() {
            val input = sampleTimeBasedUuid
            val expected = Instant.parse("2019-08-15T04:42:45.1885735Z")
            val output = input.instant()
            assertEquals(expected, output)
        }

        @Test
        fun non_time_based() {
            assertThrows<UnsupportedOperationException> {
                UUID.randomUUID().instant()
            }
        }

    }

    @Nested
    inner class Generate {

        val stressTestSize = 2.toBigInteger().pow(20).toInt()
        val stressTestParallelism = 8

        @Test
        fun result_is_time_based() {
            assertThat(generateTimeBasedUuid()).matches { it.isTimeBased }
        }

        @Test
        fun variant_is_correct() {
            assertThat(generateTimeBasedUuid()).extracting { it.variant() }.isEqualTo(2)
        }

        @Test
        fun timestamp_in_bounds() {
            val start = Instant.now()
            val result = generateTimeBasedUuid().instant()
            val end = Instant.now()
            assertThat(result).isBetween(start, end)
        }

        @Test
        fun timestamps_increasing() {
            val timestamps = (1..10).map { generateTimeBasedUuid().timestamp() }
            assertThat(timestamps).apply {
                isSorted
                doesNotHaveDuplicates()
            }
        }

        @RepeatedTest(10)
        fun check_order_sequential() {
            var prevItem: UUID = generateTimeBasedUuid()
            repeat(stressTestSize) {
                val nextItem = generateTimeBasedUuid()
                assertThat(nextItem.timestamp()).isGreaterThan(prevItem.timestamp())
                prevItem = nextItem
            }
        }

        @Test
        fun check_speed_sequential() {
            // For warm up
            check_order_sequential()
            withRetry(5) {
                // Theoretical limit is 10 000 UUIDs per ms,
                // provided that clock sequence and node are constant
                // This code must perform at most 3x slower
                assertTimeout(Duration.ofMillis(stressTestSize / 10_000L * 3)) {
                    check_order_sequential()
                }
            }
        }

        private fun <R> withRetry(count: Int, block: () -> R): R {
            require(count > 0)
            var exception: Throwable? = null
            for (i in 1..count) {
                try {
                    return block()
                } catch (e: Throwable) {
                    exception = e
                }
            }
            throw exception!!
        }

        @RepeatedTest(10)
        fun check_order_parallel() {
            val results = arrayOfNulls<UUID>(stressTestSize)
            val itemsPerRunner = stressTestSize / stressTestParallelism
            runBlocking(Executors.newFixedThreadPool(stressTestParallelism).asCoroutineDispatcher()) {
                repeat(stressTestParallelism) { runnerIndex ->
                    launch {
                        val runnerResults = arrayOfNulls<UUID>(itemsPerRunner)
                        var prevItem: UUID = generateTimeBasedUuid()
                        repeat(runnerResults.size) { i ->
                            val nextItem = generateTimeBasedUuid()
                            runnerResults[i] = nextItem
                            assertThat(nextItem.timestamp()).isGreaterThan(prevItem.timestamp())
                            prevItem = nextItem
                        }
                        val runnerOffset = itemsPerRunner * runnerIndex
                        runnerResults.copyInto(results, runnerOffset)
                    }
                }
            }
            val resultSet = results
                .mapIndexed { index, item -> checkNotNull(item) { "null at $index" } }
                .toSet()
            assertThat(resultSet).hasSameSizeAs(results)
        }

    }

}
