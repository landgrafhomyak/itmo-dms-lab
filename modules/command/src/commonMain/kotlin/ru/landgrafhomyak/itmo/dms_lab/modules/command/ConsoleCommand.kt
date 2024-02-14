package ru.landgrafhomyak.itmo.dms_lab.modules.command

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

interface ConsoleCommand {
    val name: String
    val description: String
    suspend fun execute(
        storage: StorageClientLayer,
        io: ConsoleCommandIoProvider,
        environment: ConsoleCommandEnvironment
    )
}