package ru.kontur.kinfra.commons.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withContext
import ru.kontur.kinfra.commons.time.MonotonicInstant.Companion.now
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Represents a moment of time when a coroutine's timeout will expire.
 *
 * Purpose of this class is to inform a code running in a coroutine on the time when it should complete its execution.
 *
 * For this to work [withDeadlineAfter] should be used instead of [withTimeout] across the entire codebase.
 *
 * **Warning**: don't use `withContext(deadline)`, as it does not setup timeout
 * and unconditionally replaces current deadline with a given one.
 * Always use `withDeadline()` instead.
 *
 * Usage:
 *
 *   1. Run with timeout:
 *      ```
 *      withDeadlineAfter(Duration.ofSeconds(10)) {
 *          ....
 *          println("Time left: " + coroutineContext[Deadline]?.timeLeft())
 *      }
 *      ```
 *
 *   2. Run with a specified deadline:
 *      ```
 *      val deadline = ...
 *      withDeadline(deadline) {
 *          ...
 *      }
 *      ```
 */
public class Deadline private constructor(
    private val time: MonotonicInstant
) : CoroutineContext.Element, Comparable<Deadline> {

    public fun isPassed(): Boolean = timeLeft() <= Duration.ZERO

    /**
     * Returns an amount of time before this deadline will [pass][isPassed].
     *
     * Negative values represent the time elapsed since this deadline is passed.
     */
    public fun timeLeft(): Duration {
        // Result is truncated because withTimeout's precision is a millisecond
        return (time - now()).truncatedTo(ChronoUnit.MILLIS)
    }

    public operator fun plus(offset: Duration): Deadline = Deadline(time + offset)

    public operator fun minus(offset: Duration): Deadline = Deadline(time - offset)

    override fun compareTo(other: Deadline): Int {
        return time.compareTo(other.time)
    }

    override val key: CoroutineContext.Key<*>
        get() = Deadline

    override fun equals(other: Any?): Boolean {
        return other is Deadline && other.time == this.time
    }

    override fun hashCode(): Int {
        return time.hashCode()
    }

    override fun toString(): String {
        return "Deadline($time)"
    }

    public companion object : CoroutineContext.Key<Deadline> {

        /**
         * Returns a [Deadline] that passes after given [timeout] from now.
         *
         * The timeout **must** be positive.
         */
        public fun after(timeout: Duration): Deadline {
            require(timeout > Duration.ZERO) { "Timeout must be positive: $timeout" }
            return Deadline(now() + timeout)
        }

    }

}

/**
 * Runs a given suspending [block] of code inside a coroutine with a specified [deadline][Deadline]
 * and throws a [TimeoutException] when the deadline passes.
 *
 * If current deadline is less than the specified one, it will be used instead.
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R> withDeadline(deadline: Deadline, block: suspend CoroutineScope.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val currentDeadline = coroutineContext[Deadline]
    val newDeadline = currentDeadline
        ?.let { minOf(it, deadline) }
        ?: deadline

    // withTimeout is not used because of https://github.com/Kotlin/kotlinx.coroutines/issues/1374
    val timeout = newDeadline.timeLeft()
    val result = withTimeoutOrNull(timeout) {
        Optional.ofNullable(withContext(newDeadline, block))
    }
    if (result != null) {
        // "unchecked" cast to nullable type
        return result.orElse(null)
    } else {
        throw TimeoutException("Timed out waiting for ${timeout.toMillis()} ms")
    }
}

/**
 * Shortcut for `withDeadline(Deadline.after(duration))`.
 *
 * @see withDeadline
 * @see Deadline.after
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R> withDeadlineAfter(timeout: Duration, block: suspend CoroutineScope.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val deadline = Deadline.after(timeout)
    return withDeadline(deadline, block)
}
