package ru.landgrafhomyak.itmo.dms_lab.modules.action

import ru.landgrafhomyak.itmo.dms_lab.modules.console.StopConsoleInteraction
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

interface Action {
    val name: String
    val description: String
    @Throws(StopConsoleInteraction::class)
    suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment)
}