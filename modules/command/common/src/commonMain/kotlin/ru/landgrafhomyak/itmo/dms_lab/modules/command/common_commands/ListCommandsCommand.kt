package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

class ListCommandsCommand(private val maxDescriptionWidth: UInt = 120u) : ConsoleCommand {
    override val name: String
        get() = "help"
    override val description: String
        get() = "Prints all available commands"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        io.finishArgsReading()
        val indentedConsole = PrefixedConsoleWrapper("    ", io)
        for (command in environment.commandsSet) {
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.println(command.name)
            io.setStyle(ConsoleTextStyle.TIP)
            command.description
                .chunked(this.maxDescriptionWidth.toInt())
                .forEach { line -> indentedConsole.println(line) }
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.print("\n")
        }
    }
}