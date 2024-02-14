package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object HistoryCommand : ConsoleCommand {
    override val name: String
        get() = "history"
    override val description: String
        get() = "List of last invoked commands ordered from older to newer"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        if (io.assertNoArgs()) return
        io.setStyle(ConsoleTextStyle.TIP)
        io.println("List of last ${environment.commandHistory.count()} invoked commands ordered from older to newer")
        io.setStyle(ConsoleTextStyle.DEFAULT)
        for (a in environment.commandHistory) {
            io.println(a.name)
        }
    }
}