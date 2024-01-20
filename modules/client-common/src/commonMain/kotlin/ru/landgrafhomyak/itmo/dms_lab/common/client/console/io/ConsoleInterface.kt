package ru.landgrafhomyak.itmo.dms_lab.common.client.console.io

interface ConsoleInterface {
    @Suppress("SpellCheckingInspection")
    fun readln():String?

    fun print(s: String)

    fun println(s: String)

    fun setColor(color: ConsoleColor)

    fun setUnderline(underline: Boolean)
}