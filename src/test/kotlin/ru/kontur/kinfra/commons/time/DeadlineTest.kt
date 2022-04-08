package ru.kontur.kinfra.commons.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.util.concurrent.TimeoutException

class DeadlineTest {

    @Test
    fun plus_minus() {
        val duration = Duration.ofSeconds(1)
        val sample = Deadline.after(duration)
        val earlier = sample - duration
        val later = sample + duration

        assertThat(earlier).isLessThan(sample)
        assertThat(later).isGreaterThan(sample)
        assertThat(earlier + duration.multipliedBy(2)).isEqualTo(later)
    }

    @Test
    fun is_passed_nanosecond() {
        val deadline = Deadline.after(Duration.ofNanos(1))
        assertThat(deadline).matches { it.isPassed() }
    }

    @Test
    fun is_passed_second() {
        val deadline = Deadline.after(Duration.ofSeconds(1))
        assertThat(deadline).matches { !it.isPassed() }
    }

    @Test
    fun withDeadline_use_earlier() = runBlocking<Unit> {
        val earlier = Deadline.after(Duration.ofSeconds(1))
        val later = Deadline.after(Duration.ofSeconds(2))

        withDeadline(earlier) {
            withDeadline(later) {
                assertThat(coroutineContext[Deadline]).isEqualTo(earlier)
            }
        }

        withDeadline(later) {
            withDeadline(earlier) {
                assertThat(coroutineContext[Deadline]).isEqualTo(earlier)
            }
        }
    }

    @Test
    fun withDeadline_return_value(): Unit = runBlocking {
        val result = withDeadlineAfter(Duration.ofSeconds(1)) {
            "foo"
        }
        assertThat(result).isEqualTo("foo")
    }

    @Test
    fun withDeadline_return_null_value(): Unit = runBlocking {
        val result = withDeadlineAfter<String?>(Duration.ofSeconds(1)) {
            null
        }
        assertThat(result).isNull()
    }

    @Test
    fun withDeadline_timeout(): Unit = runBlocking {
        assertThrows<TimeoutException> {
            withDeadlineAfter(Duration.ofMillis(1)) {
                delay(2)
            }
        }
    }

    @Test
    fun time_left_instantly() {
        val timeout = Duration.ofSeconds(1)
        val deadline = Deadline.after(timeout)
        val timeLeft = deadline.timeLeft()
        val maxDifference = Duration.ofMillis(20)

        assertThat(timeLeft).isLessThanOrEqualTo(timeout)
        assertThat(timeLeft).isGreaterThan(timeout - maxDifference)
    }

    @Test
    fun time_left_after_delay() {
        val timeout = Duration.ofSeconds(1)
        val deadline = Deadline.after(timeout)

        val delay = Duration.ofMillis(50)
        Thread.sleep(delay.toMillis())
        val timeLeft = deadline.timeLeft()

        assertThat(timeLeft).isLessThanOrEqualTo(timeout - delay)
        assertThat(timeLeft).isGreaterThan(timeout - delay.multipliedBy(2))
    }

    @Test
    fun zero_timeout_forbidden() {
        assertThrows<IllegalArgumentException> {
            Deadline.after(Duration.ZERO)
        }
    }

    @Test
    fun negative_timeout_forbidden() {
        assertThrows<IllegalArgumentException> {
            Deadline.after(Duration.ofSeconds(1).negated())
        }
    }

}
