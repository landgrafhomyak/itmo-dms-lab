package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityMutator {
    val descriptor: EntityDescriptor
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.Nullable): EntityMutator?
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.NotNull): EntityMutator
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute.Nullable, value: Long?)
    operator fun set(attribute: EntityAttributeDescriptor.IntAttribute, value: Long)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute.Nullable, value: Double?)
    operator fun set(attribute: EntityAttributeDescriptor.FloatAttribute, value: Double)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute.Nullable, value: String?)
    operator fun set(attribute: EntityAttributeDescriptor.StringAttribute, value: String)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute.Nullable, value: Boolean?)
    operator fun set(attribute: EntityAttributeDescriptor.BooleanAttribute, value: Boolean)
    operator fun <T : Enum<T>> set(attribute: EntityAttributeDescriptor.EnumAttribute.Nullable<T>, value: T?)
    operator fun <T : Enum<T>> set(attribute: EntityAttributeDescriptor.EnumAttribute<T>, value: T)
}