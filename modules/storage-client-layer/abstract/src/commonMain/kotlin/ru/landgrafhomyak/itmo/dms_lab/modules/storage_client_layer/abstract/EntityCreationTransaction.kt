package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor

/**
 * Transaction for creating new entity.
 */
interface EntityCreationTransaction : EntityMutator {
    /**
     * Aborts transaction.
     */
    fun cancelCreating()

    /**
     * Passes a created object to storage and closes transaction.
     */
    suspend fun finishCreating()

    /**
     * Passes a created entity to storage ***if it is [less][EntityDescriptor.compare] than any object in storage***
     * @return `true` if object stored.
     */
    suspend fun finishCreatingIfMin(): Boolean

    /**
     * Passes a created entity to storage ***if it is [greater][EntityDescriptor.compare] than any object in storage***
     * @return `true` if object stored.
     */
    suspend fun finishCreatingIfMax(): Boolean
}