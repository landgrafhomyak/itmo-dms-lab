package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor


interface FilterReceiver {

    /**
     * Selects all entities in storage. Must be called if no one other filter is applied.
     */
    suspend fun all()


    /**
     * Selects only entities whose [attribute][attr] lower than [specified value][value].
     */
    suspend fun <T : Comparable<T>> filterLower(attr: EntityAttributeDescriptor<T, *>, value: T)


    /**
     * Selects only entities whose [attribute][attr] equals to [specified value][value].
     */
    suspend fun <T : Comparable<T>> filterEqual(attr: EntityAttributeDescriptor<T, *>, value: T?)

    /**
     * Selects only entities whose [attribute][attr] greater than [specified value][value].
     */
    suspend fun <T : Comparable<T>> filterGreater(attr: EntityAttributeDescriptor<T, *>, value: T)

    /**
     * Selects only entities that are [lower][EntityDescriptor.compare] than [specified][than].
     */
    suspend fun filterLower(than: EntityAccessor)

    /**
     * Selects only entities that are [greater][EntityDescriptor.compare] than [specified][than].
     */
    suspend fun filterGreater(than: EntityAccessor)

    /**
     * Selects only a first object in a set formed by other filters.
     */
    suspend fun firstOnly()
    /**
     * Selects only a last object in a set formed by other filters.
     */
    suspend fun lastOnly()
}