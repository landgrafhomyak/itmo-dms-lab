package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object ShuffleCommand : ConsoleCommand {
    override val name: String
        get() = "shuffles"
    override val description: String
        get() = "Reorders entities in storage in random order"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        if (io.finishArgsReading()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println("Shuffling...")
        storage.reverseInline()
        io.println("Shuffled!")
    }
}