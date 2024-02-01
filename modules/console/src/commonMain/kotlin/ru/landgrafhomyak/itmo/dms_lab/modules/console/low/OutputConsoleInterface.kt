package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

interface OutputConsoleInterface {
    fun print(s: String)

    fun println(s: String)

    fun setStyle(style: ConsoleTextStyle)
}