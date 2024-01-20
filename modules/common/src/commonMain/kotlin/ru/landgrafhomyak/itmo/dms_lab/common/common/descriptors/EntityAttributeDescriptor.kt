package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors


@Suppress("RemoveRedundantQualifierName", "EqualsOrHashCode")
sealed class EntityAttributeDescriptor(val isNullable: Boolean) {
    final override fun equals(other: Any?): Boolean = this === other

    sealed class InnerEntity(
        val targetEntity: EntityDescriptor,
        isNullable: Boolean
    ) : EntityAttributeDescriptor(isNullable) {

        class Nullable(targetEntity: EntityDescriptor) : EntityAttributeDescriptor.InnerEntity(targetEntity, true)

        class NotNull(targetEntity: EntityDescriptor) : EntityAttributeDescriptor.InnerEntity(targetEntity, false)
    }

    sealed class IntAttribute(isNullable: Boolean) : EntityAttributeDescriptor(isNullable) {
        abstract fun checkValid(value: Long): Boolean

        abstract class Nullable : EntityAttributeDescriptor.IntAttribute(true)

        abstract class NotNull : EntityAttributeDescriptor.IntAttribute(false)
    }

    sealed class FloatAttribute(isNullable: Boolean) : EntityAttributeDescriptor(isNullable) {
        abstract fun checkValid(value: Double): Boolean

        abstract class Nullable : EntityAttributeDescriptor.FloatAttribute(true)

        abstract class NotNull : EntityAttributeDescriptor.FloatAttribute(false)
    }

    sealed class StringAttribute(isNullable: Boolean) : EntityAttributeDescriptor(isNullable) {
        abstract fun checkValid(value: String): Boolean

        abstract class Nullable : EntityAttributeDescriptor.StringAttribute(true)

        abstract class NotNull : EntityAttributeDescriptor.StringAttribute(false)
    }

    sealed class BooleanAttribute(isNullable: Boolean) : EntityAttributeDescriptor(isNullable) {
        object Nullable : EntityAttributeDescriptor.BooleanAttribute(true)

        object NotNull : EntityAttributeDescriptor.BooleanAttribute(false)
    }
}