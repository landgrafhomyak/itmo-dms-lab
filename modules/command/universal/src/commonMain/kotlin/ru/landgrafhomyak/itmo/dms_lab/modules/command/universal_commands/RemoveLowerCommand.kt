package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.common_filters.SelectLowerThanEntityFilter

class RemoveLowerCommand(rootEntityDescriptor: EntityDescriptor) : AbstractRemoveByEntityFilterCommand(rootEntityDescriptor) {

    override val name: String
        get() = "remove_lower"
    override val description: String
        get() = "Removes all entities that are lower than entity provided as argument"
    override val _startingMessage: String
        get() = "Removing elements that are lower than specified..."

    override fun buildFilter(entity: EntityAccessor): Filter =
        SelectLowerThanEntityFilter(entity)
}