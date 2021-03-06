[![Kotlin](https://img.shields.io/badge/Kotlin-1.6.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
![Github stable release](https://img.shields.io/github/v/release/landgrafhomyak/itmo-dms-lab?label=Stable%20release)
![Github release](https://img.shields.io/github/v/release/landgrafhomyak/itmo-dms-lab?include_prereleases&label=Latest%20release)
[![Publishing to Maven Central](https://github.com/landgrafhomyak/itmo-dms-lab/actions/workflows/publish.yml/badge.svg)](https://github.com/landgrafhomyak/itmo-dms-lab/actions/workflows/publish.yml)

[![Kotlin/MPP](https://img.shields.io/badge/Kotlin/MPP-7F52FF.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Kotlin/JVM](https://img.shields.io/badge/Kotlin/JVM-7F52FF.svg)](https://kotlinlang.org/docs/jvm-get-started.html)
[![Kotlin/Native](https://img.shields.io/badge/Kotlin/Native-7F52FF.svg)](https://kotlinlang.org/docs/native-get-started.html)
[![Kotlin/JS](https://img.shields.io/badge/Kotlin/JS-7F52FF.svg)](https://kotlinlang.org/docs/js-get-started.html)

[![Core on Maven Central](https://img.shields.io/maven-central/v/io.github.landgrafhomyak.itmo/dms-lab-core?label=Core%20on%20Maven%20Central)]()
[![Testing core](https://github.com/landgrafhomyak/itmo-dms-lab/actions/workflows/core-test.yml/badge.svg)](https://github.com/landgrafhomyak/itmo-dms-lab/actions/workflows/core-test.yml)
[![Publishing core snapshots](https://github.com/landgrafhomyak/itmo-dms-lab/actions/workflows/core-publish-snapshot.yml/badge.svg)](https://github.com/landgrafhomyak/itmo-dms-lab/actions/workflows/core-publish-snapshot.yml)
<!--[![](https://jitpack.io/v/landgrafhomyak/itmo-dms-lab.svg)](https://jitpack.io/#landgrafhomyak/itmo-dms-lab)-->


# Лабораторные работы №5-8 по программированию на факультете программной инженерии и компьютерной техники в [ИТМО](http://itmo.ru)

> **Запрещается продажа и/или использование данного кода для получения зачёта путём сдачи практику**

## Текст задания

* [Источник](https://se.ifmo.ru/courses/programming)
* Вариант № `444588`

### Объектная модель

```kotlin
data class LabWork {
    val id: Long // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    val name: String // Строка не может быть пустой
    val coordinates: Coordinates
    val creationDate: kotlinx.datetime.Instant //  Значение этого поля должно генерироваться автоматически (оригинальный тип: java.time.ZonedDateTime creationDate)
    val minimalPoint: Long // Значение поля должно быть больше 0
    val maximumPoint: Double // Значение поля должно быть больше 0
    val personalQualitiesMaximum: Int // Значение поля должно быть больше 0
    val difficulty: Difficulty
    val author: Person
}
data class Coordinates {
    val x: Long
    val y: Long // Максимальное значение поля: 116
}
data class Person {
    val name: String  // Строка не может быть пустой
    val weight: Float // Значение поля должно быть больше 0
    val eyeColor: EyeColor
    val hairColor: HairColor
    val nationality: Country
}
enum class Difficulty {
    HARD,
    INSANE,
    TERRIBLE
}
enum class HairColor {
    RED,
    WHITE,
    BROWN
}
enum class EyeColor {
    GREEN,
    BLACK,
    BLUE,
    YELLOW,
    WHITE
}
enum class Country {
    USA,
    GERMANY,
    CHINA,
    NORTH_KOREA,
    JAPAN
}
```

### Команды

* `help` - вывести справку по доступным запросам
* `info` - вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и
  т.д.)
* `show` - вывести в стандартный поток вывода все элементы коллекции в строковом представлении
* `add {element}` - добавить новый элемент в коллекцию
* `update id {element}` - обновить значение элемента коллекции, `id` которого равен заданному
* `remove_by_id id` - удалить элемент из коллекции по его `id`
* `clear` - очистить коллекцию
* `save` - сохранить коллекцию в файл
* `execute_script file_name` - считать и исполнить скрипт из указанного файла. В скрипте содержатся запросы в таком же
  виде, в котором их вводит пользователь в интерактивном режиме.
* `exit` - завершить программу (без сохранения в файл)
* `add_if_max {element}` - добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента
  этой коллекции _(так как ключ не указан, выбрано поле `LabWork.coordinates.x`)_
* `remove_greater {element}` - удалить из коллекции все элементы, превышающие заданный _(так как ключ не указан, выбрано
  поле `LabWork.coordinates.x`)_
* `history` - вывести последние 10 запросов (без их аргументов)
* `filter_by_difficulty difficulty` - вывести элементы, значение поля `difficulty` которых равно заданному
* `print_descending` - вывести элементы коллекции в порядке убывания _(так как ключ не указан, выбрано
  поле `LabWork.coordinates.x`)_
* `print_field_descending_maximum_point` - вывести значения поля `maximumPoint` всех элементов в порядке убывания

### Лабораторная работа №5

> Реализовать консольное приложение, которое реализует управление коллекцией объектов в интерактивном режиме. В коллекции необходимо хранить объекты класса LabWork, описание которого приведено ниже.
> * Класс, коллекцией экземпляров которого управляет программа, должен реализовывать сортировку по умолчанию.
> * Все требования к полям класса (указанные в виде комментариев) должны быть выполнены.
> * Для хранения необходимо использовать коллекцию типа `java.util.LinkedHashSet` _(заменён [`io.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackSetWithKeyAccess`](/common/src/commonMain/kotlin/com/github/landgrafhomyak/itmo/dms_lab/collections/Wrappers.kt))_
> * При запуске приложения коллекция должна автоматически заполняться значениями из файла.
> * Имя файла должно передаваться программе с помощью: аргумент командной строки.
> * Данные должны храниться в файле в формате `csv` _(дополнительно поддерживаются форматы `xml`, `json`)_
> * Чтение данных из файла необходимо реализовать с помощью класса `java.io.InputStreamReader` _(только для [Kotlin/JVM](https://kotlinlang.org/docs/jvm-get-started.html))_
> * Запись данных в файл необходимо реализовать с помощью класса `java.io.FileOutputStream` _(только для [Kotlin/JVM](https://kotlinlang.org/docs/jvm-get-started.html))_
> * Все классы в программе должны быть задокументированы в формате `javadoc` _(заменён на `dokka`)_.
> * Программа должна корректно работать с неправильными данными (ошибки пользовательского ввода, отсутствие прав доступа к файлу и т.п.).