package ru.landgrafhomyak.itmo.dms_lab.common.client.connection

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface RemoteStorage {
    /**
     * Returns range of entries from server.
     * [EntitiesRange.release] must be called instead of [EntityInstance.release].
     * Contained entities are updated by [RemoteStorage.waitForUpdates].
     */
    suspend fun getEntities(descriptor: EntityDescriptor, offset: ULong, size: UInt): EntitiesRange

    @Suppress("FunctionName")
    fun _startEntityUpdate(entity: EntityInstance): EntityUpdateTransaction

    @Suppress("FunctionName")
    fun _startCreatingEntity(): EntityUpdateTransaction

    suspend fun deleteEntity(entity: EntityInstance)

    suspend fun waitForUpdates(): List<EntityInstance>
}

@OptIn(ExperimentalContracts::class)
suspend inline fun RemoteStorage.updateEntity(entity: EntityInstance, transactionScope: (EntityUpdateTransaction) -> Unit) {
    contract {
        callsInPlace(transactionScope, InvocationKind.EXACTLY_ONCE)
    }

    val transaction = this._startEntityUpdate(entity)
    try {
        transactionScope(transaction)
        transaction.commit()
    } catch (e1: Throwable) {
        try {
            transaction.rollback()
        } catch (e2: Throwable) {
            e1.addSuppressed(e2)
        }
    }
}
@OptIn(ExperimentalContracts::class)
suspend inline fun RemoteStorage.createEntity(transactionScope: (EntityUpdateTransaction) -> Unit) {
    contract {
        callsInPlace(transactionScope, InvocationKind.EXACTLY_ONCE)
    }

    val transaction = this._startCreatingEntity()
    try {
        transactionScope(transaction)
        transaction.commit()
    } catch (e1: Throwable) {
        try {
            transaction.rollback()
        } catch (e2: Throwable) {
            e1.addSuppressed(e2)
        }
    }
}