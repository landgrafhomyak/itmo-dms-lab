package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor


interface FilterBuilder {

    /**
     * Selects all entities in storage. Must be called if no one other filter is applied.
     */
    fun all(): FilterBuilder


    /**
     * Selects only entities whose [attribute][attr] lower than [specified value][value].
     */
    fun <T : Comparable<T>> filterLower(attr: EntityAttributeDescriptor<T, *>, value: T): FilterBuilder


    /**
     * Selects only entities whose [attribute][attr] equals to [specified value][value].
     */
    fun <T : Comparable<T>> filterEqual(attr: EntityAttributeDescriptor<T, *>, value: T): FilterBuilder

    /**
     * Selects only entities whose [attribute][attr] greater than [specified value][value].
     */
    fun <T : Comparable<T>> filterGreater(attr: EntityAttributeDescriptor<T, *>, value: T): FilterBuilder

    /**
     * Selects only entities that are [lower][EntityDescriptor.compare] than [specified][than].
     */
    fun filterLower(than: EntityAccessor): FilterBuilder

    /**
     * Selects only entities that are [greater][EntityDescriptor.compare] than [specified][than].
     */
    fun filterGreater(than: EntityAccessor): FilterBuilder

    /**
     * Selects only a first object in a set formed by other filters.
     */
    fun firstOnly(): FilterBuilder

    fun build(): Filter
}