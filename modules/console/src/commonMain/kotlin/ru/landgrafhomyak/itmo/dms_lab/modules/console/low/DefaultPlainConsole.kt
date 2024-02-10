package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

@Suppress("RemoveRedundantQualifierName")
object DefaultPlainConsole : ConsoleInterface {
    override suspend fun readln(): String? = kotlin.io.readlnOrNull()

    override suspend fun print(s: String) = kotlin.io.print(s)

    override suspend fun println(s: String) = kotlin.io.println(s)

    override suspend fun setStyle(style: ConsoleTextStyle) {}
}