package ru.landgrafhomyak.itmo.dms_lab.modules.command

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

interface ConsoleCommandIoProvider : ConsoleInterface {
    /**
     * @return `true` if entity filled successfully
     */
    suspend fun fillEntity(mutator: EntityMutator): Boolean

    /**
     * @return `true` if function failed (e.g., unread args left)
     */
    suspend fun finishArgsReading(): Boolean
}