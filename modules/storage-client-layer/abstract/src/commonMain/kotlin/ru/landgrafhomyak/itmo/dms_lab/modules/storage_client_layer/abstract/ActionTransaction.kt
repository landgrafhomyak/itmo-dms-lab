package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor

/**
 * Transaction object providing some actions for collected via
 * [FilterTransaction] or [StorageClientLayer.actionById] entities.
 */
interface ActionTransaction {
    /**
     * Deletes collected objects and closes transaction.
     */
    suspend fun delete()

    /**
     * Returns object that allows to read collected objects.
     *
     * If descriptors passed as arguments, returns only specified attributes (similar to SQL's `SELECT`),
     * otherwise returns all possible attributes.
     *
     * After function calling this object becomes invalid to call any method on it.
     */
    suspend fun select(vararg attributes: EntityAttributeDescriptor<*, *>): StorageEntityFetcher

    /**
     * Returns object that allows to read and edit collected objects.
     *
     * After function calling this object becomes invalid to call any method on it.
     */
    suspend fun update(): StorageEntityUpdater

    /**
     * Returns quantity of collected objects and closes transaction.
     */
    suspend fun count(): UInt

    /**
     * Does nothing and closes transaction.
     */
    suspend fun abort()
}