package ru.landgrafhomyak.itmo.dms_lab.modules.entity


@Suppress("RemoveRedundantQualifierName", "EqualsOrHashCode")
sealed class EntityAttributeDescriptor(val name: String, val isNullable: Boolean) {
    final override fun equals(other: Any?): Boolean = this === other

    sealed class InnerEntity(
        val targetEntity: EntityDescriptor,
        name: String,
        isNullable: Boolean
    ) : EntityAttributeDescriptor(name, isNullable) {
        class Nullable(name: String, targetEntity: EntityDescriptor) : EntityAttributeDescriptor.InnerEntity(targetEntity, name, true)

        class NotNull(name: String, targetEntity: EntityDescriptor) : EntityAttributeDescriptor.InnerEntity(targetEntity, name, false)
    }

    sealed class IntAttribute(name: String, isNullable: Boolean) : EntityAttributeDescriptor(name, isNullable) {
        abstract fun checkValid(value: Long): Boolean

        abstract class Nullable(name: String) : EntityAttributeDescriptor.IntAttribute(name, true)

        abstract class NotNull(name: String) : EntityAttributeDescriptor.IntAttribute(name, false)
    }

    sealed class FloatAttribute(name: String, isNullable: Boolean) : EntityAttributeDescriptor(name, isNullable) {
        abstract fun checkValid(value: Double): Boolean

        abstract class Nullable(name: String) : EntityAttributeDescriptor.FloatAttribute(name, true)

        abstract class NotNull(name: String) : EntityAttributeDescriptor.FloatAttribute(name, false)
    }

    sealed class StringAttribute(name: String, isNullable: Boolean) : EntityAttributeDescriptor(name, isNullable) {
        abstract fun checkValid(value: String): Boolean

        abstract class Nullable(name: String) : EntityAttributeDescriptor.StringAttribute(name, true)

        abstract class NotNull(name: String) : EntityAttributeDescriptor.StringAttribute(name, false)
    }

    sealed class BooleanAttribute(name: String, isNullable: Boolean) : EntityAttributeDescriptor(name, isNullable) {
        class Nullable(name: String) : EntityAttributeDescriptor.BooleanAttribute(name, true)

        class NotNull(name: String) : EntityAttributeDescriptor.BooleanAttribute(name, false)
    }

    sealed class EnumAttribute<T : Enum<T>>(name: String, isNullable: Boolean) : EntityAttributeDescriptor(name, isNullable) {

        abstract fun valueToString(v: T): String

        abstract fun valueFromString(s: String): T?

        abstract class Nullable<T : Enum<T>>(name: String) : EntityAttributeDescriptor.EnumAttribute<T>(name, true)

        abstract class NotNull<T : Enum<T>>(name: String) : EntityAttributeDescriptor.EnumAttribute<T>(name, false)
    }
}