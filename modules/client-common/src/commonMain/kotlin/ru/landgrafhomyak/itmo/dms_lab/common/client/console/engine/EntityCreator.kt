package ru.landgrafhomyak.itmo.dms_lab.common.client.console.engine

import ru.landgrafhomyak.itmo.dms_lab.common.client.connection.EntityMutator
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor

interface EntityCreator : EntityMutator {
    fun cancel()

    override fun get(attribute: EntityAttributeDescriptor.InnerEntity.Nullable): EntityCreator?

    override fun get(attribute: EntityAttributeDescriptor.InnerEntity.NotNull): EntityCreator
}