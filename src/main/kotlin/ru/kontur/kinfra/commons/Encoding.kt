package ru.kontur.kinfra.commons

import java.util.*
import kotlin.reflect.KClass

/**
 * Transforms an object to and from an alternative form.
 *
 * @param L type of "plain" ("decoded") object
 * @param R type of "encoded" object
 */
public interface Encoding<L, R> {

    public fun encode(value: L): R

    public fun decode(value: R): L

    public companion object {

        /**
         * Returns an identity encoding: encoded and decoded form of values are identical.
         */
        public fun <T> identity(): Encoding<T, T> {
            @Suppress("UNCHECKED_CAST")
            return IdentityEncoding as Encoding<T, T>
        }

        /**
         * Creates an encoding for an [Enum] using an [encoding function][encoder].
         *
         * The resulting encoding is not guaranteed to be bijective.
         * Trying to [decode] an object without reverse mapping will lead to an [IllegalArgumentException].
         *
         * @throws IllegalStateException if there are duplicate mappings
         */
        public inline fun <reified L : Enum<L>, R> enum(noinline encoder: (L) -> R): Encoding<L, R> {
            return enum(L::class, encoder)
        }

        public fun <E : Enum<E>, T> enum(enumClass: KClass<E>, encoder: (E) -> T): Encoding<E, T> {
            val values = enumClass.java.enumConstants
            val directMapping = EnumMap<E, T>(enumClass.java)
            val reverseMapping = mutableMapOf<T, E>()
            for (value in values) {
                val code = encoder(value)
                val existingCode = directMapping.put(value, code)
                checkNotNull(existingCode == null)
                val existingValue = reverseMapping.put(code, value)
                check(existingValue == null) { "Duplicate mapping for $code: $existingValue and $value" }
            }
            return MappingEncoding(enumClass, directMapping, reverseMapping)
        }

        /**
         * Creates an encoding for an [Enum] using its [name][Enum.name] as encoded form.
         */
        public inline fun <reified E : Enum<E>> enumByName(): Encoding<E, String> {
            return enumByName(E::class)
        }

        public fun <E : Enum<E>> enumByName(enumClass: KClass<E>): Encoding<E, String> {
            val javaClass = enumClass.java
            return object : Encoding<E, String> {
                override fun encode(value: E): String = value.name
                override fun decode(value: String): E = java.lang.Enum.valueOf(javaClass, value)
            }
        }

        /**
         * Creates a bijection between two [Enum] classes using a [mapping function][mapper].
         *
         * For each value of the [left][L] type the [mapper] must supply corresponding value of the [right][R] type.
         * This way, each value of the right type must be mapped to a value of the left type without duplicates.
         *
         * @throws IllegalStateException if there are duplicate or missing mappings
         */
        public inline fun <reified L : Enum<L>, reified R : Enum<R>> enumBijection(
            noinline mapper: (L) -> R
        ): Encoding<L, R> {

            return enumBijection(L::class, R::class, mapper)
        }

        public fun <L : Enum<L>, R : Enum<R>> enumBijection(
            leftEnumClass: KClass<L>,
            rightEnumClass: KClass<R>,
            mapper: (L) -> R
        ): Encoding<L, R> {

            val leftValues = leftEnumClass.java.enumConstants.toSet()
            val rightValues = rightEnumClass.java.enumConstants.toSet()
            val directMapping = EnumMap<L, R>(leftEnumClass.java)
            val reverseMapping = EnumMap<R, L>(rightEnumClass.java)
            for (leftValue in leftValues) {
                val rightValue = mapper(leftValue)
                val existingRightValue = directMapping.put(leftValue, rightValue)
                checkNotNull(existingRightValue == null)
                val existingLeftValue = reverseMapping.put(rightValue, leftValue)
                check(existingLeftValue == null) {
                    "Duplicate mapping for $rightValue: $existingLeftValue and $leftValue"
                }
            }
            val missingRightValues = (rightValues - reverseMapping.keys).map { it.name }
            check(missingRightValues.isEmpty()) {
                "No reverse mapping defined for values: $missingRightValues"
            }
            return MappingEncoding(leftEnumClass, directMapping, reverseMapping)
        }

        /**
         * Creates a bijection between two [Enum] classes using constants' names for mapping.
         *
         * @throws IllegalStateException if constants of the enum classes are named differently
         */
        public inline fun <reified L : Enum<L>, reified R : Enum<R>> enumBijectionByName(): Encoding<L, R> {
            return enumBijectionByName(L::class, R::class)
        }

        public fun <L : Enum<L>, R : Enum<R>> enumBijectionByName(
            leftClass: KClass<L>,
            rightClass: KClass<R>
        ): Encoding<L, R> {

            return enumBijection(leftClass, rightClass) {
                val name = it.name
                try {
                    java.lang.Enum.valueOf(rightClass.java, name)
                } catch (e: IllegalArgumentException) {
                    throw IllegalStateException("Enum constant \"$name\" is missing in $rightClass")
                }
            }
        }

    }

}

private object IdentityEncoding : Encoding<Any?, Any?> {
    override fun encode(value: Any?): Any? = value
    override fun decode(value: Any?): Any? = value
}

private class MappingEncoding<E : Enum<E>, T>(
    private val enumClass: KClass<E>,
    private val directMapping: Map<E, T>,
    private val reverseMapping: Map<T, E>,
) : Encoding<E, T> {

    override fun encode(value: E): T {
        return directMapping.getValue(value)
    }

    override fun decode(value: T): E {
        return requireNotNull(reverseMapping[value]) { "Unknown value for ${enumClass.simpleName}: $value" }
    }

}
