package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.EntityCreationTransaction

object AddEntityCommand : AbstractAddEntityCommand() {
    override val name: String
        get() = "add"
    override val description: String
        get() = "Adds new entity to storage"

    override suspend fun _finishTransaction(transaction: EntityCreationTransaction) {
        transaction.finishCreating()
    }
}