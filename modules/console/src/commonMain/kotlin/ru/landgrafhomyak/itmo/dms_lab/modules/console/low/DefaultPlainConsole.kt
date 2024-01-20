package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

@Suppress("RemoveRedundantQualifierName")
object DefaultPlainConsole : ConsoleInterface {
    override fun readln(): String? = kotlin.io.readlnOrNull()

    override fun print(s: String) = kotlin.io.print(s)

    override fun println(s: String) = kotlin.io.println(s)

    override fun setStyle(style: ConsoleTextStyle) {}
}