package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.StopConsoleInteraction
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object ClearAction : Action {
    override val name: String
        get() = "clear"
    override val description: String
        get() = "Removes all entities from storage"

    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        if (io.finishArgsReading()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        io.println("Clearing storage...")
        storage.clear()
        io.println("Storage cleared")
    }
}