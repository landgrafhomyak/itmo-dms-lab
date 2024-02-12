package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

class ListActionsAction(private val maxDescriptionWidth: UInt = 120u) : Action {
    override val name: String
        get() = "help"
    override val description: String
        get() = "Prints all available actions"

    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        io.finishArgsReading()
        val indentedConsole = PrefixedConsoleWrapper("    ", io)
        for (action in environment.actionsSet) {
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.println(action.name)
            io.setStyle(ConsoleTextStyle.TIP)
            action.description
                .chunked(this.maxDescriptionWidth.toInt())
                .forEach { line -> indentedConsole.println(line) }
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.print("\n")
        }
    }
}