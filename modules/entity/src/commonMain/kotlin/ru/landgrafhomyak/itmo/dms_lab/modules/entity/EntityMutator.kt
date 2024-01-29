package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityMutator {
    val descriptor: EntityDescriptor
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.Optional): EntityMutator?
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute.Required): EntityMutator
    operator fun <T : Any> set(attribute: EntityAttributeDescriptor._Optional<*, T>, value: T?)
    operator fun <T : Any> set(attribute: EntityAttributeDescriptor<*, T>, value: T)
}