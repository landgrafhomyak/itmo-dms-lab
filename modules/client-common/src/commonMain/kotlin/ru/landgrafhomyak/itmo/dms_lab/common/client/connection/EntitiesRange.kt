package ru.landgrafhomyak.itmo.dms_lab.common.client.connection

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor

interface EntitiesRange : Iterator<EntityInstance> {

    val descriptor: EntityAttributeDescriptor

    val len: UInt
    fun release()
}