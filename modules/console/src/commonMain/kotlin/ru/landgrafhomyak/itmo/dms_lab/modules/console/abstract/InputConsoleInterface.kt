package ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract

interface InputConsoleInterface {
    @Suppress("SpellCheckingInspection")
    suspend fun readln():String?
}