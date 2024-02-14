package ru.landgrafhomyak.itmo.dms_lab.modules.entity


@Suppress("RemoveRedundantQualifierName", "EqualsOrHashCode")
sealed class EntityAttributeDescriptor<out R : Any, in W : Any>(
    val name: String,
    val isNullable: Boolean,
    val masterDescriptor: EntityDescriptor
) {
    final override fun equals(other: Any?): Boolean = this === other

    @Suppress("ClassName")
    sealed interface _Optional<out R : Any, in W : Any>

    @Suppress("ClassName")
    sealed interface _Required<out R : Any, in W : Any>

    @Suppress("ClassName", "SpellCheckingInspection")
    sealed interface _Checkable<T : Any> {
        fun checkValid(value: T): Boolean
    }

    open class ComplexAttribute(
        val targetEntityDescriptor: EntityDescriptor,
        name: String,
        masterDescriptor: EntityDescriptor
    ) : EntityAttributeDescriptor<EntityAccessor, EntityMutator>(name, false, masterDescriptor),
        _Required<EntityAccessor, EntityMutator>

    sealed class IntAttribute(
        name: String,
        isNullable: Boolean,
        masterDescriptor: EntityDescriptor
    ) : EntityAttributeDescriptor<Long, Long>(name, isNullable, masterDescriptor),
        _Checkable<Long> {
        abstract class Optional(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.IntAttribute(name, true, masterDescriptor),
            _Optional<Long, Long>

        abstract class Required(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.IntAttribute(name, false, masterDescriptor),
            _Required<EntityAccessor, EntityMutator>
    }

    sealed class FloatAttribute(
        name: String,
        isNullable: Boolean,
        masterDescriptor: EntityDescriptor
    ) : EntityAttributeDescriptor<Double, Double>(name, isNullable, masterDescriptor),
        _Checkable<Double> {
        abstract class Optional(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.FloatAttribute(name, true, masterDescriptor),
            _Optional<Double, Double>

        abstract class Required(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.FloatAttribute(name, false, masterDescriptor),
            _Required<Double, Double>
    }

    sealed class StringAttribute(
        name: String,
        isNullable: Boolean,
        masterDescriptor: EntityDescriptor
    ) : EntityAttributeDescriptor<String, String>(name, isNullable, masterDescriptor),
        _Checkable<String> {
        abstract class Optional(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.StringAttribute(name, true, masterDescriptor),
            _Optional<String, String>

        abstract class Required(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.StringAttribute(name, false, masterDescriptor),
            _Required<String, String>
    }

    sealed class BooleanAttribute(
        name: String,
        isNullable: Boolean,
        masterDescriptor: EntityDescriptor
    ) : EntityAttributeDescriptor<Boolean, Boolean>(name, isNullable, masterDescriptor) {
        class Optional(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.BooleanAttribute(name, true, masterDescriptor),
            _Optional<Boolean, Boolean>

        class Required(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.BooleanAttribute(name, false, masterDescriptor),
            _Required<Boolean, Boolean>
    }

    sealed class EnumAttribute<T : Enum<T>>(
        name: String,
        isNullable: Boolean,
        masterDescriptor: EntityDescriptor
    ) : EntityAttributeDescriptor<T, T>(name, isNullable, masterDescriptor) {

        abstract fun valueToString(v: T): String

        abstract fun valueFromString(s: String): T?

        abstract class Optional<T : Enum<T>>(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.EnumAttribute<T>(name, true, masterDescriptor),
            _Optional<T, T>

        abstract class Required<T : Enum<T>>(
            name: String,
            masterDescriptor: EntityDescriptor
        ) : EntityAttributeDescriptor.EnumAttribute<T>(name, false, masterDescriptor),
            _Required<T, T>
    }
}