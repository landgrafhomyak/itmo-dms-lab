package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.EntityCreationTransaction
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

abstract class AbstractAddEntityAction : Action {
    @Suppress("FunctionName")
    protected abstract suspend fun _finishTransaction(transaction: EntityCreationTransaction)
    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        val transaction = storage.startEntityCreating()
        try {
            io.fillEntity(transaction)
            if (io.finishArgsReading()) {
                io.setStyle(ConsoleTextStyle.ERROR)
                io.println("Entity creating failed")
                io.setStyle(ConsoleTextStyle.DEFAULT)
                return
            }
            this._finishTransaction(transaction)
        } catch (e1: Throwable) {
            try {
                io.setStyle(ConsoleTextStyle.ERROR)
                io.println("Entity creating failed")
                io.setStyle(ConsoleTextStyle.DEFAULT)
                transaction.cancelCreating()
                return
            } catch (e2: Throwable) {
                e1.addSuppressed(e2)
            }
            throw e1
        }
    }

}