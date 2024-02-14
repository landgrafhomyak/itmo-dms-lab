package ru.landgrafhomyak.itmo.dms_lab.models.demo

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor

object DemoRootEntityDescriptor : EntityDescriptor {
    override fun toString(): String =
        "<entity_descriptor 'demo'>"

    object ComparationKeyAttribute : EntityAttributeDescriptor.FloatAttribute.Required("comparation_key", DemoRootEntityDescriptor) {
        override fun checkValid(value: Double): Boolean = true
    }

    object SomeStringAttribute : EntityAttributeDescriptor.StringAttribute.Optional("some_string", DemoRootEntityDescriptor) {
        override fun checkValid(value: String): Boolean = true
    }


    object CoordinatesAttribute :
        EntityAttributeDescriptor.ComplexAttribute(CoordinatesDescriptor, "coordinates", DemoRootEntityDescriptor) {

        object XAttribute : EntityAttributeDescriptor.IntAttribute.Required("x", CoordinatesDescriptor) {
            override fun checkValid(value: Long): Boolean = value >= 0
        }

        object YAttribute : EntityAttributeDescriptor.IntAttribute.Required("y", CoordinatesDescriptor) {
            override fun checkValid(value: Long): Boolean = value >= 0
        }

    }

    object CoordinatesDescriptor: EntityDescriptor {
        override val name: String
            get() = "coordinates"

        override fun toString(): String =
            "<entity_descriptor 'demo:coordinates'>"
        override fun compare(left: EntityAccessor, right: EntityAccessor): Int = throw UnsupportedOperationException()


        private val attributes = arrayOf(CoordinatesAttribute.XAttribute, CoordinatesAttribute.YAttribute)

        override fun iterator(): Iterator<EntityAttributeDescriptor<*, *>> = this.attributes.iterator()

    }

    override val name: String
        get() = "demo_entity"

    override fun compare(left: EntityAccessor, right: EntityAccessor): Int =
        left[ComparationKeyAttribute].compareTo(right[ComparationKeyAttribute])


    private val attributes = arrayOf<EntityAttributeDescriptor<*, *>>(ComparationKeyAttribute, SomeStringAttribute, CoordinatesAttribute)

    override fun iterator(): Iterator<EntityAttributeDescriptor<*, *>> = this.attributes.iterator()
}