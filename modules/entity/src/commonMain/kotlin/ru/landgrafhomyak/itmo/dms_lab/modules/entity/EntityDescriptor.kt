package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityDescriptor : Iterable<EntityAttributeDescriptor<*, *>> {
    val name: String
    fun compare(left: EntityAccessor, right: EntityAccessor): Int
}