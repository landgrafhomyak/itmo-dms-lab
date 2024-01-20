package ru.landgrafhomyak.itmo.dms_lab.common.client.console.engine

import ru.landgrafhomyak.itmo.dms_lab.common.client.console.io.ConsoleInterface

interface CommandsContext {
    /**
     * Returns [entity constructor][EntityCreator] that must be filled by caller
     * if command expects entity (and only entity) as its argument, otherwise returns `null`.
     */
    fun isEntityCommand(commandName: String): EntityCreator?

    /**
     * Called if previous [CommandsContext.isEntityCommand] call returned creator.
     */
    fun executeCommand(console: ConsoleInterface, commandName: String, entity: EntityCreator)

    /**
     * Called if previous [CommandsContext.isEntityCommand] call returned `null`.
     */
    fun executeCommand(console: ConsoleInterface, commandName: String, args: List<String>)
}