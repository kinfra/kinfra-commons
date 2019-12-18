package ru.kontur.kinfra.commons

import ru.kontur.kinfra.commons.Either.Left
import ru.kontur.kinfra.commons.Either.Right

/**
 * Represents a value of one of two possible types (a disjoint union).
 *
 * An instance of [Either] is either an instance of [Left] or [Right]:
 * * [Left] represents an error description ([Left.error])
 * * [Right] represents a result of a successful computation ([Right.value])
 */
sealed class Either<out L, out R> {
    // Sources of inspiration:
    // - Arrow's Either
    // - kotlin.Result

    data class Left<out L>(val error: L) : Either<L, Nothing>()

    data class Right<out R>(val value: R) : Either<Nothing, R>()

    inline fun <T> fold(onFailure: (L) -> T, onSuccess: (R) -> T): T = when (this) {
        is Left -> onFailure(error)
        is Right -> onSuccess(value)
    }

    companion object {
        fun <L> left(error: L): Either<L, Nothing> = Left(error)
        fun <R> right(value: R): Either<Nothing, R> = Right(value)
    }

}

val <L, R> Either<L, R>.isLeft: Boolean
    get() = this is Left

val <L, R> Either<L, R>.isRight: Boolean
    get() = this is Right

fun <L, R> Either<L, R>.leftOrNull(): L? = fold({ it }, { null })

fun <L, R> Either<L, R>.getOrNull(): R? = fold({ null }, { it })

inline fun <L, R, T> Either<L, R>.mapLeft(transform: (L) -> T): Either<T, R> = when (this) {
    is Left -> Left(transform(error))
    is Right -> this
}

inline fun <L, R, T> Either<L, R>.map(transform: (R) -> T): Either<L, T> = when (this) {
    is Left -> this
    is Right -> Right(transform(value))
}

inline fun <R, L, T> Either<L, R>.flatMap(transform: (R) -> Either<L, T>): Either<L, T> = when (this) {
    is Left -> this
    is Right -> transform(value)
}

fun <L, R> Either<L, R>.ensureSuccess(): R = fold(
    { throw IllegalStateException("Result is error: $it") },
    { it }
)

fun <R : T, T> Either<*, R>.getOrDefault(default: T): T = fold({ default }, { it })

// todo: should it be named "getOrElse"?
inline fun <L, T, R : T> Either<L, R>.recover(transform: (L) -> T): T = when (this) {
    is Left -> transform(error)
    is Right -> value
}
