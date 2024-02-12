package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter

class CommonFilterImpl(
    override val rootEntityDescriptor: EntityDescriptor,
    private val actions: Iterable<Filter.Action>
) : Filter {
    override fun iterator(): Iterator<Filter.Action> =
        this.actions.iterator()

}