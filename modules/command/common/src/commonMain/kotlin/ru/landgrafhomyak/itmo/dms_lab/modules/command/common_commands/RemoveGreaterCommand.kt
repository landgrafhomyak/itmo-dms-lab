package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

class RemoveGreaterCommand(rootEntityDescriptor: EntityDescriptor) : AbstractRemoveByEntityFilterCommand(rootEntityDescriptor) {

    override val name: String
        get() = "remove_greater"
    override val description: String
        get() = "Removes all entities that are greater than entity provided as argument"
    override val _startingMessage: String
        get() = "Removing elements that are greater than specified..."

    override fun _buildFilter(storage: StorageClientLayer, entity: EntityAccessor): Filter =
        storage.startFilterCreating().filterGreater(entity).build()
}