package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityAccessor {
    val descriptor: EntityDescriptor
    operator fun <T : Any> get(attribute: EntityAttributeDescriptor._Optional<T, *>): T?
    operator fun <T : Any> get(attribute: EntityAttributeDescriptor._Required<T, *>): T
}