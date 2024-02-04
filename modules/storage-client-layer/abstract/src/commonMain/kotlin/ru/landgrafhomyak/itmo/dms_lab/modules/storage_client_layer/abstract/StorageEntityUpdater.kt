package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

/**
 * Mutable version of [StorageEntityReader].
 */
interface StorageEntityUpdater : EntityMutator {
    /**
     * Starts writing to next entity.
     *
     * Returns `false` if all entities already updated.
     * In this case, the transaction is closed and an object becomes invalid.
     */
    suspend fun next(): Boolean

    /**
     * If transaction not ended, closes it.
     * @throws IllegalStateException if the transaction was ended or aborted before.
     */
    suspend fun abortFetching()
}