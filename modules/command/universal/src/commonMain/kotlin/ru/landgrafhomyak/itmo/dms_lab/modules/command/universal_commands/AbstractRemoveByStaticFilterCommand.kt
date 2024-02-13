package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

abstract class AbstractRemoveByStaticFilterCommand : ConsoleCommand {
    @Suppress("PropertyName")
    protected abstract val _startingMessage: String

    @Suppress("FunctionName")
    protected abstract val filter: Filter

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        if (io.assertNoArgs()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println(this._startingMessage)
        try {
            storage.startActionByFilter(this.filter).delete()
        } catch (t: Throwable) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Failed to remove")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            throw t
        }
        io.println("Removed!")
    }
}