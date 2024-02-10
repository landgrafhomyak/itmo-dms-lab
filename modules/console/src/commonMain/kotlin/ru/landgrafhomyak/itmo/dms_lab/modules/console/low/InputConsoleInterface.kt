package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

interface InputConsoleInterface {
    @Suppress("SpellCheckingInspection")
    suspend fun readln():String?
}