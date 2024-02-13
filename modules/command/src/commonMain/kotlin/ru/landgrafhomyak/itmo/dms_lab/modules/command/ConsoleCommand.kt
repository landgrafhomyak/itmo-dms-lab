package ru.landgrafhomyak.itmo.dms_lab.modules.command

import ru.landgrafhomyak.itmo.dms_lab.modules.console.StopConsoleInteraction
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

interface ConsoleCommand {
    val name: String
    val description: String
    @Throws(StopConsoleInteraction::class)
    suspend fun execute(
        storage: StorageClientLayer,
        io: ConsoleCommandIoProvider,
        environment: ConsoleCommandEnvironment
    )
}