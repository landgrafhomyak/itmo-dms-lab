package ru.landgrafhomyak.itmo.dms_lab.common.client.connection

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor

interface EntityMutator {
    operator fun set(attribute: EntityAttributeDescriptor.InnerEntity.Nullable, value: EntityInstance?)
    operator fun set(attribute: EntityAttributeDescriptor.InnerEntity.NotNull, value: EntityInstance)
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.Nullable, value: Long?)
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.NotNull, value: Long)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.Nullable, value: Double?)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.NotNull, value: Double)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.Nullable, value: String?)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.NotNull, value: String)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.Nullable, value: Boolean?)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.NotNull, value: Boolean)
}