package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMapImpl
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

abstract class AbstractRemoveByEntityFilterAction(protected val rootEntityDescriptor: EntityDescriptor) : Action {
    @Suppress("PropertyName")
    protected abstract val _startingMessage: String

    @Suppress("FunctionName")
    protected abstract fun _buildFilter(storage: StorageClientLayer, entity: EntityAccessor): Filter

    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        val splitterEntity = EntityMapImpl(this.rootEntityDescriptor)
        io.fillEntity(splitterEntity)
        this.rootEntityDescriptor.assertAllAttributesSet(splitterEntity)
        if (io.finishArgsReading()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println(this._startingMessage)
        try {
            storage.startActionByFilter(this._buildFilter(storage, splitterEntity)).delete()
        } catch (t: Throwable) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Failed to remove")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            throw t
        }
        io.println("Removed!")
    }
}