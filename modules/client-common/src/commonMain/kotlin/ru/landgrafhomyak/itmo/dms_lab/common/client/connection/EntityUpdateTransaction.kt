package ru.landgrafhomyak.itmo.dms_lab.common.client.connection

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor

interface EntityUpdateTransaction : EntityMutator {
    val descriptor: EntityDescriptor
    suspend fun commit()
    suspend fun rollback()
}