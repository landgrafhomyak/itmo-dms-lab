package ru.landgrafhomyak.itmo.dms_lab.modules.entity

interface EntityDescriptor : Iterable<EntityAttributeDescriptor<*, *>> {
    val name: String
    fun compare(left: EntityAccessor, right: EntityAccessor): Int

    fun assertAllAttributesSet(entity: EntityAccessor) {
        for (attr in this) {
            when (attr) {
                is EntityAttributeDescriptor.ComplexAttribute ->
                    attr.targetEntityDescriptor.assertAllAttributesSet(entity[attr])

                else -> if (attr is EntityAttributeDescriptor._Required<*, *>) entity[attr]
            }
        }
    }
}