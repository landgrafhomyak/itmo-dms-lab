package ru.landgrafhomyak.itmo.dms_lab.modules.entity

class EntityMapImpl(
    override val descriptor: EntityDescriptor,
    private val data: MutableMap<EntityAttributeDescriptor<*, *>, Any?> = HashMap()
) : EntityAccessor, EntityMutator {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T? {
        this.descriptor.assertAttribute(attribute)
        return this.data[attribute] as T?
    }

    @Suppress("UNCHECKED_CAST", "INAPPLICABLE_JVM_NAME")
    @JvmName("getRequired")
    override fun <T : Any, A> get(attribute: A): T
            where A : EntityAttributeDescriptor<T, *>,
                  A : EntityAttributeDescriptor._Required<T, *> {
        this.descriptor.assertAttribute(attribute)
        return this.data[attribute]
            .let { v -> v ?: throw RuntimeException("Important attribute not set") }
            .let { v -> v as T }
    }

    override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute): EntityMutator {
        this.descriptor.assertAttribute(attribute)
        return this.data
            .getOrDefault(attribute) { HashMap<EntityAttributeDescriptor<*, *>, Any?>() }
            .let { v -> (v as? EntityMutator) ?: throw RuntimeException("Entity stored value with wrong type") }
    }

    override fun set(attribute: EntityAttributeDescriptor.ComplexAttribute.Optional, value: Nothing?) {
        this.descriptor.assertAttribute(attribute)
        this.data[attribute] = null
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("setOptional")
    override fun <T : Any, A> set(attribute: A, value: T?)
            where A : EntityAttributeDescriptor<T, *>,
                  A : EntityAttributeDescriptor._Optional<T, *> {
        this.descriptor.assertAttribute(attribute)
        if (attribute is EntityAttributeDescriptor.ComplexAttribute)
            throw IllegalArgumentException("Complex attribute must by set via .copyInto()")
        this.data[attribute] = value
    }

    override fun <T : Any> set(attribute: EntityAttributeDescriptor<T, *>, value: T) {
        this.descriptor.assertAttribute(attribute)
        if (attribute is EntityAttributeDescriptor.ComplexAttribute)
            throw IllegalArgumentException("Complex attribute must by set via .copyInto()")
        this.data[attribute] = value
    }
}