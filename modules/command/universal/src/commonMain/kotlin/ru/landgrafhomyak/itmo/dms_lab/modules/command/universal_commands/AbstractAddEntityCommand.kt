package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.EntityCreationTransaction
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

abstract class AbstractAddEntityCommand : ConsoleCommand {
    @Suppress("FunctionName")
    protected abstract suspend fun _finishTransaction(transaction: EntityCreationTransaction)
    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        val transaction = storage.startEntityCreating()
        try {
            if (!io.fillEntity(io.argsOrEmpty, transaction)) {
                io.setStyle(ConsoleTextStyle.ERROR)
                io.println("Entity creating failed")
                io.setStyle(ConsoleTextStyle.DEFAULT)
                transaction.cancelCreating()
                return
            }
            this._finishTransaction(transaction)
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.println("Created successful!")
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