package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors

interface EntityDescriptor: Iterable<EntityAttributeDescriptor> {
    val name: String
}