package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

/**
 * Mutable version of [StorageEntityReader].
 */
interface StorageEntityUpdater : EntityMutator {
    suspend fun performUpdate()

    suspend fun abort()
}