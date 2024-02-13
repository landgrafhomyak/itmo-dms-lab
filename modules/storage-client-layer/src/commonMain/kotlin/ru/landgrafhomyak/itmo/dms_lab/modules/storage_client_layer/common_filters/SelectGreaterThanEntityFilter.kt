package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.common_filters

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.FilterReceiver

class SelectGreaterThanEntityFilter(private val entity: EntityAccessor) : Filter {
    override suspend fun build(builder: FilterReceiver) {
        builder.filterGreater(entity)
    }
}