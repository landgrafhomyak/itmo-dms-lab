package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor

/**
 * Transaction to select which entities will be processed via [ActionTransaction].
 */
@Suppress("RemoveRedundantQualifierName")
interface Filter : Iterable<Filter.Action> {
    val rootEntityDescriptor: EntityDescriptor

    sealed class Action {
        @Suppress("ConvertObjectToDataObject")
        object All : Filter.Action()

        enum class ComparatorDirection {
            /**
             * Select entities that are lower than provided value
             */
            LOWER,

            /**
             * Select entities that are equals to provided value
             */
            EQUAL,

            /**
             * Select entities that are greater than provided value
             */
            GREATER
        }

        class CompareEntity(
            val direction: ComparatorDirection,
            val targetEntity: EntityAccessor
        ) : Filter.Action()

        @Suppress("MemberVisibilityCanBePrivate")
        class CompareAttribute<T : Comparable<T>>(
            val direction: ComparatorDirection,
            val attribute: EntityAttributeDescriptor<T, *>,
            val value: T
        ) : Filter.Action() {
            fun compare(entity: EntityAccessor): Int? {
                return entity[this.attribute]?.compareTo(this.value)
            }
        }

        class CompareAttributeNull(
            val attribute: EntityAttributeDescriptor<*, *>,
        ) : Filter.Action()

        @Suppress("ConvertObjectToDataObject")
        object FirstOnly : Filter.Action()
    }
}