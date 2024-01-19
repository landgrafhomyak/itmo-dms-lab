package ru.landgrafhomyak.itmo.dms_lab.common.common.storage

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor

interface EntityInstance {
    val descriptor: EntityAttributeDescriptor
    operator fun get(attribute: EntityAttributeDescriptor.Reference.Nullable): EntityInstance?
    operator fun get(attribute: EntityAttributeDescriptor.Reference.NotNull): EntityInstance
    operator fun get(attribute: EntityAttributeDescriptor.IntAttribute.Nullable): Long?
    operator fun get(attribute: EntityAttributeDescriptor.IntAttribute.NotNull): Long
    operator fun get(attribute: EntityAttributeDescriptor.FloatAttribute.Nullable): Double?
    operator fun get(attribute: EntityAttributeDescriptor.FloatAttribute.NotNull): Double
    operator fun get(attribute: EntityAttributeDescriptor.StringAttribute.Nullable): String?
    operator fun get(attribute: EntityAttributeDescriptor.StringAttribute.NotNull): String
    operator fun get(attribute: EntityAttributeDescriptor.BooleanAttribute.Nullable): Boolean?
    operator fun get(attribute: EntityAttributeDescriptor.BooleanAttribute.NotNull): Boolean
}