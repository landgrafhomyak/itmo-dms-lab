package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand

object RemoveTailCommand : ConsoleCommand by RemoveLastCommand {
    override val name: String
        get() = "remove_tail"
}