package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityAccessor {
    val descriptor: EntityAttributeDescriptor
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.Nullable): EntityAccessor?
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.NotNull): EntityAccessor
    operator fun get(attribute: EntityAttributeDescriptor.IntAttribute.Nullable): Long?
    operator fun get(attribute: EntityAttributeDescriptor.IntAttribute.NotNull): Long
    operator fun get(attribute: EntityAttributeDescriptor.FloatAttribute.Nullable): Double?
    operator fun get(attribute: EntityAttributeDescriptor.FloatAttribute.NotNull): Double
    operator fun get(attribute: EntityAttributeDescriptor.StringAttribute.Nullable): String?
    operator fun get(attribute: EntityAttributeDescriptor.StringAttribute.NotNull): String
    operator fun get(attribute: EntityAttributeDescriptor.BooleanAttribute.Nullable): Boolean?
    operator fun get(attribute: EntityAttributeDescriptor.BooleanAttribute.NotNull): Boolean
    operator fun <T:Enum<T>>get(attribute: EntityAttributeDescriptor.EnumAttribute.Nullable<T>): T?
    operator fun <T:Enum<T>>get(attribute: EntityAttributeDescriptor.EnumAttribute.NotNull<T>): T
}