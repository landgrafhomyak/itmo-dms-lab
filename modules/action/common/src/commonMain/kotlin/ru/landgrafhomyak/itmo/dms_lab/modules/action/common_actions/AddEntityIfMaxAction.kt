package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.EntityCreationTransaction

object AddEntityIfMaxAction:AbstractAddEntityAction() {
    override val name: String
        get() = "add_if_max"
    override val description: String
        get() = "Adds new entity to storage if it is greater than all other entities in storage"
    override suspend fun _finishTransaction(transaction: EntityCreationTransaction) {
        transaction.finishCreatingIfMax()
    }
}