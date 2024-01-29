package ru.landgrafhomyak.itmo.dms_lab.modules.entity


@Suppress("RemoveRedundantQualifierName", "EqualsOrHashCode")
sealed class EntityAttributeDescriptor<out R : Any, in W : Any>(val name: String, val isNullable: Boolean) {
    final override fun equals(other: Any?): Boolean = this === other

    @Suppress("ClassName")
    sealed interface _Required<out R : Any, in W : Any>

    @Suppress("ClassName")
    sealed interface _Optional<out R : Any, in W : Any>

    @Suppress("ClassName", "SpellCheckingInspection")
    sealed interface _Checkable<T : Any> {
        abstract fun checkValid(value: T): Boolean
    }

    sealed class ComplexAttribute(
        val targetEntity: EntityDescriptor,
        name: String,
        isNullable: Boolean
    ) : EntityAttributeDescriptor<EntityAccessor, EntityMutator>(name, isNullable) {
        class Optional(name: String, targetEntity: EntityDescriptor) :
            EntityAttributeDescriptor.ComplexAttribute(targetEntity, name, true),
            _Optional<EntityAccessor, EntityMutator>

        class Required(name: String, targetEntity: EntityDescriptor) :
            EntityAttributeDescriptor.ComplexAttribute(targetEntity, name, false),
            _Required<EntityAccessor, EntityAccessor>
    }

    sealed class IntAttribute(name: String, isNullable: Boolean) :
        EntityAttributeDescriptor<Long, Long>(name, isNullable),
        _Checkable<Long> {
        abstract class Optional(name: String) :
            EntityAttributeDescriptor.IntAttribute(name, true),
            _Optional<Long, Long>

        abstract class Required(name: String) :
            EntityAttributeDescriptor.IntAttribute(name, false),
            _Required<Long, Long>
    }

    sealed class FloatAttribute(name: String, isNullable: Boolean) :
        EntityAttributeDescriptor<Double, Double>(name, isNullable),
        _Checkable<Double> {
        abstract class Optional(name: String) :
            EntityAttributeDescriptor.FloatAttribute(name, true),
            _Optional<Double, Double>

        abstract class Required(name: String) :
            EntityAttributeDescriptor.FloatAttribute(name, false),
            _Required<Double, Double>
    }

    sealed class StringAttribute(name: String, isNullable: Boolean) :
        EntityAttributeDescriptor<String, String>(name, isNullable),
        _Checkable<String> {
        abstract class Optional(name: String) :
            EntityAttributeDescriptor.StringAttribute(name, true),
            _Optional<String, String>

        abstract class Required(name: String) :
            EntityAttributeDescriptor.StringAttribute(name, false),
            _Required<String, String>
    }

    sealed class BooleanAttribute(name: String, isNullable: Boolean) : EntityAttributeDescriptor<Boolean, Boolean>(name, isNullable) {
        class Optional(name: String) :
            EntityAttributeDescriptor.BooleanAttribute(name, true),
            _Optional<Boolean, Boolean>

        class Required(name: String) :
            EntityAttributeDescriptor.BooleanAttribute(name, false),
            _Required<Boolean, Boolean>
    }

    sealed class EnumAttribute<T : Enum<T>>(name: String, isNullable: Boolean) : EntityAttributeDescriptor<T, T>(name, isNullable) {

        abstract fun valueToString(v: T): String

        abstract fun valueFromString(s: String): T?

        abstract class Optional<T : Enum<T>>(name: String) :
            EntityAttributeDescriptor.EnumAttribute<T>(name, true),
            _Optional<T, T>

        abstract class Required<T : Enum<T>>(name: String) :
            EntityAttributeDescriptor.EnumAttribute<T>(name, false),
            _Required<T, T>
    }
}