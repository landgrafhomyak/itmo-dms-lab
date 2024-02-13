package ru.landgrafhomyak.itmo.dms_lab.modules.entity

class RequiredAttributeNotSetError(
    val attribute: EntityAttributeDescriptor<*, *>,
    val entity: EntityAccessor
): RuntimeException("Required attribute `${attribute.name}` not set")