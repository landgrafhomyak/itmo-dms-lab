package ru.landgrafhomyak.itmo.dms_lab.common.client.console.engine

import ru.landgrafhomyak.itmo.dms_lab.common.client.connection.EntityMutator
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor

interface EntityCreator : EntityMutator {
    val descriptor: EntityDescriptor

    fun cancel()
}