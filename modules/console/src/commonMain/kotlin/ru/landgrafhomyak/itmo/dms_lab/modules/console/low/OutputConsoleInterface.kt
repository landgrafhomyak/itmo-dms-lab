package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

interface OutputConsoleInterface {
    suspend fun print(s: String)

    suspend fun println(s: String)

    suspend fun setStyle(style: ConsoleTextStyle)
}