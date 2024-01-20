package ru.landgrafhomyak.itmo.dms_lab.modules.console.engine

import ru.landgrafhomyak.itmo.dms_lab.modules.console.low.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

@Suppress("RemoveRedundantQualifierName")
interface CommandsContext {
    /**
     * Type-alias interface to avoid possible errors at compile time.
     *
     * Instances of this interface should be context-less: creator can
     * be dropped without notifying.
     */
    interface EntityCreatorForCommand : EntityMutator {
        override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.Nullable): CommandsContext.EntityCreatorForCommand?
        override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.NotNull): CommandsContext.EntityCreatorForCommand
    }

    /**
     * Returns [entity constructor][CommandsContext.EntityCreatorForCommand] that must be filled by caller
     * if command expects entity (and only entity) as its argument, otherwise returns `null`.
     */
    fun isEntityCommand(commandName: String):  CommandsContext.EntityCreatorForCommand?

    /**
     * Called if previous [CommandsContext.isEntityCommand] call returned creator.
     */
    fun executeCommand(console: ConsoleInterface, commandName: String, entity:  CommandsContext.EntityCreatorForCommand)

    /**
     * Called if previous [CommandsContext.isEntityCommand] call returned `null`.
     */
    fun executeCommand(console: ConsoleInterface, commandName: String, args: List<String>)
}