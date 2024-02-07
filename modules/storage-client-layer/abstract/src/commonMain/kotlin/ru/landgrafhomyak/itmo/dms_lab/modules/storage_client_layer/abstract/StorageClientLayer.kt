package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.CommonFilterBuilderImpl

/**
 * Client's interface to access storage (doesn't matter is it in memory, in file or remote)
 */
interface StorageClientLayer {

    /**
     * Descriptor of elements in this storage.
     */
    val rootEntityDescriptor: EntityDescriptor


    /**
     * Commit all changes into storage.
     */
    suspend fun commit()

    /**
     * Discards all changes since previous commit or rollback.
     */
    fun rollback()

    fun clear()

    /**
     * Starts transaction for creating entity.
     */
    fun startEntityCreating(): EntityCreationTransaction


    fun startFilterCreating(): FilterBuilder = CommonFilterBuilderImpl(this.rootEntityDescriptor)

    /**
     * Starts universal transaction.
     */
    suspend fun startActionByFilter(filter: Filter): ActionTransaction

    /**
     * Starts transaction for an object with specified [ordinal][id]
     */
    suspend fun startActionById(id: UInt): ActionTransaction?

    /**
     * Groups entities by [specified attribute][attr] and returns quantity of entities in each group.
     */
    suspend fun <T : Any> countByGroup(attr: EntityAttributeDescriptor<T, *>): CountByGroupFetcher<T>

    /**
     * Reorders entities in storage in random order.
     */
    suspend fun shuffleInline()

    /**
     * Reverses order of entities in storage
     */
    suspend fun reverseInline()
}