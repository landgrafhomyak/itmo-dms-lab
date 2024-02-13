package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.common_filters.SelectFirstFilter

object RemoveFirstCommand : AbstractRemoveByStaticFilterCommand() {
    override val name: String
        get() = "remove_first"
    override val description: String
        get() = "Removes first element from storage"


    override val _startingMessage: String
        get() = "Removing first..."
    override val filter: Filter
        get() = SelectFirstFilter
}