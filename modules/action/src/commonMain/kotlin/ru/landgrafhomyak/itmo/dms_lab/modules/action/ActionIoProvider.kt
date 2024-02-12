package ru.landgrafhomyak.itmo.dms_lab.modules.action

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

interface ActionIoProvider : ConsoleInterface {
    /**
     * @return `true` if entity filled successfully
     */
    suspend fun fillEntity(mutator: EntityMutator): Boolean

    /**
     * @return `true` if function failed (e.g., unread args left)
     */
    suspend fun finishArgsReading(): Boolean
}