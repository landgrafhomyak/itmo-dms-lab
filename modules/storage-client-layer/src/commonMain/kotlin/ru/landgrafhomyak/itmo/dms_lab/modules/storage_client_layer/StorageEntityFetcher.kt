package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

/**
 * Similar to Java's `ResultRow`. Provides access to [collected][ActionTransaction.select] entities.
 */
interface StorageEntityFetcher : EntityAccessor {
    /**
     * Fetches next entity.
     *
     * Returns `false` if all entities already fetched.
     * In this case, the transaction is closed and an object becomes invalid.
     */
    /* suspend */ fun next()

    /**
     * If transaction not ended, closes it.
     * @throws IllegalStateException if the transaction was ended or aborted before.
     */
    /* suspend */ fun abortFetching()

    /**
     * Copies all attributes of the current entity into specified mutator.
     */
    fun copyInto(mutator: EntityMutator)
}