package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.EntityCreationTransaction

object AddEntityIfMinCommand:AbstractAddEntityCommand() {
    override val name: String
        get() = "add_if_min"
    override val description: String
        get() = "Adds new entity to storage if it is less than all other entities in storage"
    override suspend fun _finishTransaction(transaction: EntityCreationTransaction) {
        transaction.finishCreatingIfMin()
    }
}