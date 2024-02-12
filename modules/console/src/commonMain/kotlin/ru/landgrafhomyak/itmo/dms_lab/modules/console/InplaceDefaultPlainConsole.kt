package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle

@Suppress("RemoveRedundantQualifierName")
object InplaceDefaultPlainConsole : ConsoleInterface {
    override suspend fun readln(): String? = kotlin.io.readlnOrNull()

    override suspend fun print(s: String) = kotlin.io.print(s)

    override suspend fun println(s: String) = kotlin.io.println(s)

    override suspend fun setStyle(style: ConsoleTextStyle) {}
}