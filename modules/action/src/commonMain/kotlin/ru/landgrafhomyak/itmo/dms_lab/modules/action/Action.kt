package ru.landgrafhomyak.itmo.dms_lab.modules.action

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

interface Action {
    val name: String
    val description: String
    suspend fun executeIO(set: ActionsSet, storage: StorageClientLayer, io: ActionIOProvider)
}