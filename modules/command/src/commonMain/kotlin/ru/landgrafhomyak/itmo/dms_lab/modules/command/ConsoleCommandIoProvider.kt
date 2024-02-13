package ru.landgrafhomyak.itmo.dms_lab.modules.command

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

interface ConsoleCommandIoProvider : ConsoleInterface {
    /**
     * @param firstLine string with raw attribute value for root entity, if passed `null`,
     * they will be obtained via [ConsoleCommandIoProvider.readln]
     * @return `true` if entity filled successfully
     */
    suspend fun fillEntity(firstLine: String?, target: EntityMutator): Boolean

    val argsOrNull: String?
    val argsOrEmpty: String get() = this.argsOrNull ?: ""

    /**
     * Asserts that no args specified to the command.
     * @return `true` if assertion failed (e.g., unread args left)
     */
    suspend fun assertNoArgs(): Boolean
}