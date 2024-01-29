package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityDescriptor : Iterable<EntityAttributeDescriptor<Any, Any>> {
    val name: String
}