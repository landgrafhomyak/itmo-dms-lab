package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

class RemoveLowerAction(rootEntityDescriptor: EntityDescriptor) : AbstractRemoveByEntityFilterAction(rootEntityDescriptor) {

    override val name: String
        get() = "remove_lower"
    override val description: String
        get() = "Removes all entities that are lower than entity provided as argument"
    override val _startingMessage: String
        get() = "Removing elements that are lower than specified..."

    override fun _buildFilter(storage: StorageClientLayer, entity: EntityAccessor): Filter =
        storage.startFilterCreating().filterLower(entity).build()
}