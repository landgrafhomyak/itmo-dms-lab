package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.common_filters.SelectLastFilter

object RemoveLastCommand : AbstractRemoveByStaticFilterCommand() {
    override val name: String
        get() = "remove_first"
    override val description: String
        get() = "Removes last element from storage"


    override val _startingMessage: String
        get() = "Removing last..."
    override val filter: Filter
        get() = SelectLastFilter
}