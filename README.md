kinfra-commons
==============

Общий вспомогательный код для библиотек и приложений на Kotlin.

Описание возможностей
---------------------

### Расширения примитивных типов

#### Boolean.thenTake()

Для `true` вызывает переданную лямбду и возвращает её значение.
Для `false` сразу возвращает `null`.

#### String.prefixNotEmpty()

Добавляет префикс к непустой строке. Пустую строку возвращает как есть.

### Расширения классов JDK

#### Optional.unwrap()

Более безопасная альтернатива `Optional.orElse(null)`: возвращаемый тип - `T?`, а не `T!`.

### Работа с time-based UUID

#### generateTimeBasedUuid()

Создаёт новый time-based UUID.

#### UUID.isTimeBased

Определяет, является ли данный UUID time-based (версии 1).

#### UUID.instant()

Возвращает `Instant`, соответствующий полю `timestamp`.

### Работа с `enum class`

### Enum.lowerCaseName

Возвращает название константы `enum` в нижнем регистре.

### Работа со временем

#### Класс MonotonicInstant

Аналог `Instant` на основе `System.nanoTime()` для измерения прошедшего времени.

#### Класс Deadline

Предназначен для передачи в вызываемый код информации о том, когда истечёт таймаут его выполнения.

Для корректной работы этого механизма необходимо использовать функцию `withDeadlineAfter()` вместо `withTimeout()`.

Пример использования:

    withDeadlineAfter(Duration.ofSeconds(10)) {
        ....
        println("Time left: " + coroutineContext[Deadline]?.timeLeft())
    }

#### Класс TimeTicks

Представляет время в тиках (100 наносекунд)
и позволяет конвертировать `Instant` или `Duration` в количество тиков и обратно.

Подклассы:

 * `TimeSpan` - промежуток времени (аналог `Duration`)
 * `Timestamp` - момент времени (аналог `Instant`)

`Timestamp` имеет 3 подкласса для разных точек отсчёта:

 * `DotNetTimestamp` - 01.01.0001 (используется в .NET)
 * `UuidTimestamp` - 15.10.1582 (используется в UUID)
 * `EpochTimestamp` - 01.01.1970 (используется в Vostok Hercules)

Экземпляры данных классов могут быть созданы из количества тиков или следующими методами:

 * `Duration.toTicks()`
 * `Instant.toDotNetTime()`
 * `Instant.toUuidTime()`
 * `Instant.toEpochTicks()`

### Работа с двоичными данными

#### Byte.asUnsigned()

Беззнаковое представление 8 бит из `Byte`

#### Short.asUnsigned()

Беззнаковое представление 16 бит из `Short`

#### Конвертация в шестнадцатеричное представление и обратно

 * `Byte.toHexString()`
 * `ByteArray.toHexString()` и `byteArrayOfHex()`
 * `StringBuilder.appendHexByte()`

### Either<L, R>

Представляет выбор между ошибочным состоянием (L) и успешным получением результата (R).

Сборка
------

    ./gradlew build
