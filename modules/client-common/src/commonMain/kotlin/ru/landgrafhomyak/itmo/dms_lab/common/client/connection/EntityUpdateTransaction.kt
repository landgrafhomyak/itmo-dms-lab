package ru.landgrafhomyak.itmo.dms_lab.common.client.connection

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor

interface EntityUpdateTransaction : EntityMutator {
    suspend fun commit()
    suspend fun rollback()
}