package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor

/**
 * Similar to [StorageEntityFetcher], but for accessing groups.
 */
interface CountByGroupFetcher<T : Any> {
    /**
     * Attribute descriptor used for grouping.
     */
    val attribute: EntityAttributeDescriptor<T, *>

    /**
     * Quantity of entities in a group with key `null`.
     * [.next()][CountByGroupFetcher.next] call doesn't affect this value.
     */
    val nullCount: UInt

    /**
     * Key of a current group.
     */
    val groupValue: T


    /**
     * Quantity of entities in a current group.
     */
    val count: UInt

    /**
     * Fetches next group.
     *
     * Returns `false` if all groups already fetched.
     * In this case, the transaction is closed and an object becomes invalid.
     */
    suspend fun next(): Boolean

    /**
     * If transaction not ended, closes it.
     * @throws IllegalStateException if the transaction was ended or aborted before.
     */
    suspend fun abortFetching()
}