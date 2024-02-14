package ru.landgrafhomyak.itmo.dms_lab.models.demo

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor

object DemoRootEntityDescriptor : EntityDescriptor {
    object ComparationKeyAttribute : EntityAttributeDescriptor.FloatAttribute.Required("comparation_key", DemoRootEntityDescriptor) {
        override fun checkValid(value: Double): Boolean = true
    }

    object SomeStringAttribute : EntityAttributeDescriptor.StringAttribute.Optional("some_string", DemoRootEntityDescriptor) {
        override fun checkValid(value: String): Boolean = true
    }


    object CoordinatesAttribute :
        EntityAttributeDescriptor.ComplexAttribute(this, "coordinates", DemoRootEntityDescriptor),
        EntityDescriptor {
        override fun compare(left: EntityAccessor, right: EntityAccessor): Int = throw UnsupportedOperationException()

        object XAttribute : EntityAttributeDescriptor.IntAttribute.Required("x", CoordinatesAttribute) {
            override fun checkValid(value: Long): Boolean = value >= 0
        }

        object YAttribute : EntityAttributeDescriptor.IntAttribute.Required("y", CoordinatesAttribute) {
            override fun checkValid(value: Long): Boolean = value >= 0
        }

        private val attributes = arrayOf(XAttribute, YAttribute)

        override fun iterator(): Iterator<EntityAttributeDescriptor<*, *>> = this.attributes.iterator()
    }

    override val name: String
        get() = "demo_entity"

    override fun compare(left: EntityAccessor, right: EntityAccessor): Int =
        left[ComparationKeyAttribute].compareTo(right[ComparationKeyAttribute])


    private val attributes = arrayOf<EntityAttributeDescriptor<*, *>>(ComparationKeyAttribute, SomeStringAttribute, CoordinatesAttribute)

    override fun iterator(): Iterator<EntityAttributeDescriptor<*, *>> = this.attributes.iterator()
}