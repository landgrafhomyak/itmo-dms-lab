package ru.landgrafhomyak.itmo.dms_lab.modules.action

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

interface ActionIOProvider : ConsoleInterface {
    suspend fun fillEntity(mutator: EntityMutator)
}