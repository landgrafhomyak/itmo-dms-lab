package ru.landgrafhomyak.itmo.dms_lab.common.common.storage

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor

interface EntityUpdateTransaction {
    suspend fun commit()
    suspend fun rollback()
    operator fun set(attribute: EntityAttributeDescriptor.Reference.Nullable, value: EntityInstance?)
    operator fun set(attribute: EntityAttributeDescriptor.Reference.NotNull, value: EntityInstance)
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.Nullable, value: Long?)
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.NotNull, value: Long)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.Nullable, value: Double?)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.NotNull, value: Double)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.Nullable, value: String?)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.NotNull, value: String)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.Nullable, value: Boolean?)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.NotNull, value: Boolean)
}