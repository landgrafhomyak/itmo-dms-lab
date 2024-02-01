package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator

/**
 * Mutable version of [StorageEntityFetcher].
 */
interface StorageEntityUpdater : StorageEntityFetcher, EntityMutator