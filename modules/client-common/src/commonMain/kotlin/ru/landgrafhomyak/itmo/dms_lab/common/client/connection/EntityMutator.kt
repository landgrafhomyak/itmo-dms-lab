package ru.landgrafhomyak.itmo.dms_lab.common.client.connection

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor

interface EntityMutator {
    val descriptor: EntityDescriptor
    operator fun get(attribute: EntityAttributeDescriptor.InnerEntity.Nullable): EntityMutator?
    operator fun get(attribute: EntityAttributeDescriptor.InnerEntity.NotNull): EntityMutator
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.Nullable, value: Long?)
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.NotNull, value: Long)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.Nullable, value: Double?)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.NotNull, value: Double)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.Nullable, value: String?)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.NotNull, value: String)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.Nullable, value: Boolean?)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.NotNull, value: Boolean)
}