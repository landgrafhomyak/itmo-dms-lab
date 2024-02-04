package ru.landgrafhomyak.itmo.dms_lab.modules.entity

@Suppress("NOTHING_TO_INLINE")
inline operator fun EntityDescriptor.contains(attr: EntityAttributeDescriptor<*, *>): Boolean =
    attr.masterDescriptor === attr

@Suppress("NOTHING_TO_INLINE")
inline fun EntityDescriptor.assertAttribute(attr: EntityAttributeDescriptor<*, *>) =
    require(attr in this) { "Entity doesn't contains requested attribute" }