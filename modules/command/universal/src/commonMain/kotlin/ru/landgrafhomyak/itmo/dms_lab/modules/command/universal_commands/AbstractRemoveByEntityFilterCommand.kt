package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMapImpl
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

abstract class AbstractRemoveByEntityFilterCommand(
    @Suppress("MemberVisibilityCanBePrivate")
    protected val rootEntityDescriptor: EntityDescriptor
) : ConsoleCommand {
    @Suppress("PropertyName")
    protected abstract val _startingMessage: String

    protected abstract fun buildFilter(entity: EntityAccessor): Filter

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        val splitterEntity = EntityMapImpl(this.rootEntityDescriptor)
        io.fillEntity(io.argsOrEmpty, splitterEntity)
        this.rootEntityDescriptor.assertAllAttributesSet(splitterEntity)
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println(this._startingMessage)
        try {
            storage.startActionByFilter(this.buildFilter(splitterEntity)).delete()
        } catch (t: Throwable) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Failed to remove")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            throw t
        }
        io.println("Removed!")
    }
}