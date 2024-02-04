package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor

/**
 * Similar to Java's `ResultRow`. Provides access to [collected][ActionTransaction.select] entities.
 */
interface StorageEntityReader : EntityAccessor {

    val id: UInt

    /**
     * Fetches next entity.
     *
     * Returns `false` if all entities already fetched.
     * In this case, the transaction is closed and an object becomes invalid.
     */
    suspend fun next(): Boolean

    /**
     * If transaction not ended, closes it.
     * @throws IllegalStateException if the transaction was ended or aborted before.
     */
    suspend fun abortFetching()
}