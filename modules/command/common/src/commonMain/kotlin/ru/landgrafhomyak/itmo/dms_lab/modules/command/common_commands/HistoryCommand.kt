package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object HistoryCommand : ConsoleCommand {
    override val name: String
        get() = "save"
    override val description: String
        get() = "Saves all changes to file (from which it was loaded)"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        if (io.finishArgsReading()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        for (a in environment.commandHistory) {
            io.println(a.name)
        }
    }
}