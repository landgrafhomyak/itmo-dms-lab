package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand

object RemoveHeadCommand : ConsoleCommand by RemoveFirstCommand {
    override val name: String
        get() = "remove_head"
}