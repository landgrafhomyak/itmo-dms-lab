package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.common_filters

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.FilterReceiver

object SelectAllFilter : Filter {
    override suspend fun build(builder: FilterReceiver) {
        builder.all()
    }
}