@file:OptIn(ExperimentalContracts::class)

package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@PublishedApi
internal inline fun <T> StorageClientLayer._createEntity(
    userScope: (EntityMutator) -> Unit,
    commit: EntityCreationTransaction.() -> T
): T {
    contract {
        callsInPlace(userScope, InvocationKind.EXACTLY_ONCE)
        callsInPlace(commit, InvocationKind.EXACTLY_ONCE)
    }
    val transaction = this.startEntityCreating()
    try {
        userScope(transaction)
        return commit(transaction)
    } catch (e1: Throwable) {
        try {
            transaction.cancelCreating()
        } catch (e2: Throwable) {
            e1.addSuppressed(e2)
        }
        throw e1
    }
}

/**
 * Creates an entity and passes it to storage.
 */
suspend inline fun StorageClientLayer.createEntity(scope: (EntityMutator) -> Unit) {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    this._createEntity(scope) { finishCreating() }
}

/**
 * Creates an entity and passes it to storage ***if it is [less][EntityDescriptor.compare] than any object in storage***.
 * @return `true` if object stored.
 */
suspend inline fun StorageClientLayer.createEntityIfMin(scope: (EntityMutator) -> Unit): Boolean {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    return this._createEntity(scope) { finishCreatingIfMin() }
}

/**
 * Creates an entity and passes it to storage ***if it is [greater][EntityDescriptor.compare] than any object in storage***.
 * @return `true` if object stored.
 */
suspend inline fun StorageClientLayer.createEntityIfMax(scope: (EntityMutator) -> Unit): Boolean {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    return this._createEntity(scope) { finishCreatingIfMax() }
}

/**
 * Groups entities by [specified attribute][attr] and returns quantity of entities in each group.
 */
suspend inline fun <T : Any> StorageClientLayer.countByGroup(
    attr: EntityAttributeDescriptor<T, *>,
    groupReceiver: (value: T?, quantity: UInt) -> Unit
) {
    contract {
        callsInPlace(groupReceiver)
    }
    val transaction = this.countByGroup(attr)
    try {
        if (transaction.nullCount > 0u)
            groupReceiver(null, transaction.nullCount)

        while (transaction.next())
            groupReceiver(transaction.groupValue, transaction.count)

    } catch (e1: Throwable) {
        try {
            transaction.abortFetching()
        } catch (e2: Throwable) {
            e1.addSuppressed(e2)
        }
        throw e1
    }
}


suspend inline fun StorageEntityReader.readRemainingSafe(
    receiver: (attributes: EntityAccessor) -> Unit
) {
    contract {
        callsInPlace(receiver)
    }
    try {
        while (this.next())
            receiver(this)
    } catch (e1: Throwable) {
        try {
            this.abortFetching()
        } catch (e2: Throwable) {
            e1.addSuppressed(e2)
        }
        throw e1
    }
}

suspend fun StorageClientLayer.deleteByFilter(filter: Filter) {
    this.startActionByFilter(filter).delete()
}

suspend fun StorageClientLayer.countByFilter(filter: Filter): UInt {
    return this.startActionByFilter(filter).count()
}

suspend inline fun StorageClientLayer.readByFilter(
    filter: Filter,
    vararg attributes: EntityAttributeDescriptor<*, *>,
    receiver: (attributes: EntityAccessor) -> Unit
) {
    this.startActionByFilter(filter).select(*attributes).readRemainingSafe(receiver)
}


suspend inline fun StorageClientLayer.updateByFilter(
    filter: Filter,
    receiver: (attributes: EntityMutator) -> Unit
) {
    val transaction = this.startActionByFilter(filter).update()
    try {
        receiver(transaction)
        transaction.performUpdate()
    } catch (e: Throwable) {
        transaction.abort()
        throw e
    }
}
