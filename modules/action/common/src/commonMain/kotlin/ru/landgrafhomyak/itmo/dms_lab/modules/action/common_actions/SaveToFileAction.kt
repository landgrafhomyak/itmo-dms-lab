package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.StopConsoleInteraction
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

class SaveToFileAction(private val filename: String? = null) : Action {
    override val name: String
        get() = "save"
    override val description: String
        get() = "Saves all changes to file (from which it was loaded)"

    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        io.setStyle(ConsoleTextStyle.DEFAULT)
        if (this.filename == null)
            io.println("Saving storage...")
        else
            io.println("Saving storage to '${this.filename}'...")
        storage.commit()
        io.println("Storage saved successfully!")
    }
}