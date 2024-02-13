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

    @Suppress("FunctionName")
    private suspend inline fun _executeFiltered(
        io: ConsoleCommandIoProvider,
        environment: ConsoleCommandEnvironment,
        filter: (String) -> Boolean
    ) {
        val indentedConsole = PrefixedConsoleWrapper("    ", io)
        var anyCommandPrinted = false
        for (command in environment.commandsSet) {
            if (!filter(command.name)) continue
            anyCommandPrinted = true
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.println(command.name)
            io.setStyle(ConsoleTextStyle.TIP)
            command.description
                .chunked(this.maxDescriptionWidth.toInt())
                .forEach { line -> indentedConsole.println(line) }
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.print("\n")
        }
        if (!anyCommandPrinted) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("No commands found for specified query")
            io.setStyle(ConsoleTextStyle.TIP)
            io.println("Try to invoke this command without query to see all commands")
            io.setStyle(ConsoleTextStyle.DEFAULT)
        }
    }

    override suspend fun execute(
        storage: StorageClientLayer,
        io: ConsoleCommandIoProvider,
        environment: ConsoleCommandEnvironment
    ) {
        val args = io.argsOrNull
        if (args == null) {
            this._executeFiltered(io, environment) { true }
            return
        }

        this._executeFiltered(io, environment) { name -> args in name }
    }
}