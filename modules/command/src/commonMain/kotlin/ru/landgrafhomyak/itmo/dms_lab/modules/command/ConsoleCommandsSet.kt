package ru.landgrafhomyak.itmo.dms_lab.modules.command

interface ConsoleCommandsSet : Iterable<ConsoleCommand> {
    fun dispatchCommand(name: String): ConsoleCommand?
}