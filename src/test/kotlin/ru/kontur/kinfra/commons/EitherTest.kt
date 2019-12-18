package ru.kontur.kinfra.commons

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class EitherTest {

    @TestFactory
    fun fold() = testBoth(
        { input ->
            val result = input.fold(
                { assertEquals("error", it); "result" },
                { error("should not be called") }
            )

            assertEquals("result", result)
        },
        { input ->
            val result = input.fold(
                { error("should not be called") },
                { assertEquals("value", it); "result" }
            )

            assertEquals("result", result)
        }
    )

    @TestFactory
    fun isLeft() = testBoth(
        { input ->
            assertTrue(input.isLeft)
        },
        { input ->
            assertFalse(input.isLeft)
        }
    )

    @TestFactory
    fun isRight() = testBoth(
        { input ->
            assertFalse(input.isRight)
        },
        { input ->
            assertTrue(input.isRight)
        }
    )

    @TestFactory
    fun leftOrNull() = testBoth(
        { input ->
            assertEquals("error", input.leftOrNull())
        },
        { input ->
            assertNull(input.leftOrNull())
        }
    )

    @TestFactory
    fun getOrNull() = testBoth(
        { input ->
            assertNull(input.getOrNull())
        },
        { input ->
            assertEquals("value", input.getOrNull())
        }
    )

    @TestFactory
    fun mapLeft() = testBoth(
        { input ->
            val result = input.mapLeft { error ->
                assertEquals("error", error)
                "new error"
            }

            assertEquals(Either.left("new error"), result)
        },
        { input ->
            val result = input.mapLeft {
                error("should not be called")
            }

            assertEquals(input, result)
        }
    )

    @TestFactory
    fun map() = testBoth(
        { input ->
            val result = input.map {
                error("should not be called")
            }

            assertEquals(input, result)
        },
        { input ->
            val result = input.map { value ->
                assertEquals("value", value)
                "new value"
            }

            assertEquals(Either.right("new value"), result)
        }
    )

    @TestFactory
    fun ensureSuccess() = testBoth(
        { input ->
            assertThrows<IllegalStateException> {
                input.ensureSuccess()
            }
        },
        { input ->
            assertEquals("value", input.ensureSuccess())
        }
    )

    @TestFactory
    fun getOrDefault() = testBoth(
        { input ->
            assertEquals("default", input.getOrDefault("default"))
        },
        { input ->
            assertEquals("value", input.getOrDefault("default"))
        }
    )

    @TestFactory
    fun flatMap() = testBoth(
        { input ->
            val result = input.flatMap<String, String, String> {
                error("should not be called")
            }

            assertEquals(input, result)
        },
        { input ->
            val result = input.flatMap { value ->
                assertEquals("value", value)
                Either.right("next value")
            }

            assertEquals(Either.right("next value"), result)
        }
    )

    @TestFactory
    fun getOrElse() = testBoth(
        { input ->
            val result = input.getOrElse { error ->
                assertEquals("error", error)
                "fallback"
            }

            assertEquals("fallback", result)
        },
        { input ->
            val result = input.getOrElse {
                error("should not be called")
            }

            assertEquals("value", result)
        }
    )

    private fun testBoth(
        testLeft: (input: Either<String, String>) -> Unit,
        testRight: (input: Either<String, String>) -> Unit
    ): Collection<DynamicTest> {

        return listOf(
            DynamicTest.dynamicTest("left") {
                testLeft(Either.left("error"))
            },
            DynamicTest.dynamicTest("right") {
                testRight(Either.right("value"))
            }
        )
    }

}
