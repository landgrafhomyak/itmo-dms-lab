package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

interface ConsoleInterface {
    @Suppress("SpellCheckingInspection")
    fun readln():String?

    fun print(s: String)

    fun println(s: String)

    fun setStyle(style: ConsoleTextStyle)
}