package ru.kontur.kinfra.commons

import ru.kontur.kinfra.commons.Either.Companion.left
import ru.kontur.kinfra.commons.Either.Companion.right
import ru.kontur.kinfra.commons.Either.Left
import ru.kontur.kinfra.commons.Either.Right

/**
 * Represents a value of one of two possible types (a disjoint union).
 *
 * An instance of [Either] is either an instance of [Left] or [Right]:
 * * [Left] represents an error description ([Left.error])
 * * [Right] represents a result of a successful computation ([Right.value])
 */
public sealed class Either<out L, out R> {
    // Sources of inspiration:
    // - Arrow's Either
    // - kotlin.Result

    public data class Left<out L>(val error: L) : Either<L, Nothing>()

    public data class Right<out R>(val value: R) : Either<Nothing, R>()

    public inline fun <T> fold(onFailure: (L) -> T, onSuccess: (R) -> T): T = when (this) {
        is Left -> onFailure(error)
        is Right -> onSuccess(value)
    }

    public companion object {

        public fun <L> left(error: L): Either<L, Nothing> = Left(error)

        public fun <R> right(value: R): Either<Nothing, R> = Right(value)

    }

}

public val <L, R> Either<L, R>.isLeft: Boolean
    get() = this is Left

public val <L, R> Either<L, R>.isRight: Boolean
    get() = this is Right

public fun <L, R> Either<L, R>.leftOrNull(): L? = fold({ it }, { null })

public inline fun <L, R, T> Either<L, R>.mapLeft(transform: (L) -> T): Either<T, R> = when (this) {
    is Left -> left(transform(error))
    is Right -> this
}

public inline fun <L, R, T> Either<L, R>.map(transform: (R) -> T): Either<L, T> = when (this) {
    is Left -> this
    is Right -> right(transform(value))
}

public inline fun <L, R, T> Either<L, R>.flatMap(transform: (R) -> Either<L, T>): Either<L, T> = when (this) {
    is Left -> this
    is Right -> transform(value)
}

public fun <R> Either<*, R>.getOrNull(): R? = getOrDefault(null)

public fun <R> Either<*, R>.getOrDefault(default: R): R = getOrElse { default }

public inline fun <L, R> Either<L, R>.getOrThrow(exceptionSource: (L) -> Exception): R {
    return getOrElse { throw exceptionSource(it) }
}

public inline fun <L, R> Either<L, R>.getOrElse(transform: (L) -> R): R = when (this) {
    is Left -> transform(error)
    is Right -> value
}

public fun <R> Either<*, R>.checkRight(): R = getOrThrow { IllegalStateException("Result is error: $it") }

@Deprecated("use checkRight()", ReplaceWith("checkRight()"))
public fun <R> Either<*, R>.ensureSuccess(): R = checkRight()
