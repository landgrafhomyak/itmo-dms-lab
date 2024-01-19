package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors

sealed interface EntityAttributeDescriptor {
    val isNullable: Boolean

    interface Reference : EntityAttributeDescriptor {
        val targetEntity: EntityDescriptor
    }

    interface IntAttribute : EntityAttributeDescriptor {
        fun checkValid(value: Long): Boolean
    }

    interface FloatAttribute : EntityAttributeDescriptor {
        fun checkValid(value: Double): Boolean
    }

    interface StringAttribute : EntityAttributeDescriptor {
        fun checkValid(value: String): Boolean
    }

    sealed interface BooleanAttribute : EntityAttributeDescriptor
}