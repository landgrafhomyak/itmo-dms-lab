package ru.landgrafhomyak.itmo.dms_lab.common.client.console.io

object DefaultPlainConsole : ConsoleInterface {
    override fun readln(): String? = readlnOrNull()

    override fun print(s: String) = kotlin.io.print(s)

    override fun println(s: String) = kotlin.io.println(s)

    override fun setColor(color: ConsoleColor) {}

    override fun setUnderline(underline: Boolean) {}
}