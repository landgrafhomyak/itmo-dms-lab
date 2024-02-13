package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object ClearCommand : ConsoleCommand {
    override val name: String
        get() = "clear"
    override val description: String
        get() = "Removes all entities from storage"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        if (io.assertNoArgs()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println("Clearing storage...")
        storage.clear()
        io.println("Storage cleared")
    }
}