package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

abstract class AbstractRemoveByStaticFilterAction : Action {
    @Suppress("PropertyName")
    protected abstract val _startingMessage: String
    @Suppress("FunctionName")
    protected abstract fun _buildFilter(storage: StorageClientLayer): Filter

    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        if (io.finishArgsReading()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println(this._startingMessage)
        try {
            storage.startActionByFilter(this._buildFilter(storage)).delete()
        } catch (t: Throwable) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Failed to remove")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            throw t
        }
        io.println("Removed!")
    }
}