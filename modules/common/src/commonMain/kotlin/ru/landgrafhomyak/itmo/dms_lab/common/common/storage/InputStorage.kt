package ru.landgrafhomyak.itmo.dms_lab.common.common.storage

import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityDescriptor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface InputStorage {
    suspend fun getEntities(descriptor: EntityDescriptor, dst: Array<in EntityDescriptor>, offset: ULong)

    @Suppress("FunctionName")
    fun _startEntityUpdate(entity: EntityInstance): EntityUpdateTransaction

    @Suppress("FunctionName")
    fun _startCreatingEntity(): EntityUpdateTransaction

    suspend fun deleteEntity(entity: EntityInstance)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun InputStorage.updateEntity(entity: EntityInstance, transactionScope: (EntityUpdateTransaction) -> Unit) {
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
suspend inline fun InputStorage.createEntity(transactionScope: (EntityUpdateTransaction) -> Unit) {
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