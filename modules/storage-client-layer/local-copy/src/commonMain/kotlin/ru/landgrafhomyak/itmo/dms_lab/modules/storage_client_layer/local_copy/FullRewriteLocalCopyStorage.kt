@file:Suppress("RemoveRedundantQualifierName")

package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.local_copy

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMapImpl
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.ActionTransaction
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.CountByGroupFetcher
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.EntityCreationTransaction
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageEntityReader
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageEntityUpdater
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class FullRewriteLocalCopyStorage private constructor(
    override val rootEntityDescriptor: EntityDescriptor,
) : StorageClientLayer {
    private val globalMutex = Mutex()
    protected abstract val checkpoint: MutableList<EntityAccessor>
    protected abstract val changes: MutableList<EntityAccessor>

    @Suppress("UNCHECKED_CAST")
    private val _checkpoint: MutableList<LocalEntity>
        get() = this.checkpoint as MutableList<LocalEntity>

    @Suppress("UNCHECKED_CAST")
    private val _changes: MutableList<LocalEntity>
        get() = this.changes as MutableList<LocalEntity>

    open override suspend fun commit() {
        this.checkpoint.clear()
        for (o in this.changes) {
            val cp = LocalEntity(true)
            o.copyInto(cp)
            cp.isMutable = false
            this.checkpoint.add(cp)
        }
    }

    private inner class LocalEntity(isMutable: Boolean = false) : EntityAccessor, EntityMutator {
        var isMutable = isMutable
            set(value) {
                for (attr in this@FullRewriteLocalCopyStorage.rootEntityDescriptor) {
                    if (attr !is EntityAttributeDescriptor.ComplexAttribute)
                        continue
                    if (attr is EntityAttributeDescriptor.ComplexAttribute.Optional) {
                        @Suppress("USELESS_CAST") // getter for complex attribute conflicts with mutator
                        if ((this as EntityAccessor)[attr] == null)
                            continue
                    }
                    @Suppress("USELESS_CAST")
                    (this as EntityMutator)[attr]
                        .let { c -> c as? LocalEntity ?: throw RuntimeException("Wrong attribute value") }
                        .isMutable = value
                    field = value
                }
            }

        private val data: MutableMap<EntityAttributeDescriptor<*, *>, Any?> = HashMap()

        @Suppress("FunctionName", "NOTHING_TO_INLINE")
        private inline fun _assertIsMutable() {
            if (!this.isMutable) throw IllegalStateException("This entity is not enabled for editing now")
        }

        override val descriptor: EntityDescriptor
            get() = this@FullRewriteLocalCopyStorage.rootEntityDescriptor


        override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute): EntityMutator {
            this._assertIsMutable()
            return this.data[attribute] as? EntityMutator ?: throw RuntimeException("Wrong attribute value")
        }

        override fun set(attribute: EntityAttributeDescriptor.ComplexAttribute.Optional, value: Nothing?) {
            this._assertIsMutable()
            this.data[attribute] = null
        }

        override fun <T : Any, A> set(attribute: A, value: T?)
                where A : EntityAttributeDescriptor<T, *>,
                      A : EntityAttributeDescriptor._Optional<T, *> {
            this._assertIsMutable()
            this.data[attribute] = value
        }

        override fun <T : Any> set(attribute: EntityAttributeDescriptor<T, *>, value: T) {
            this._assertIsMutable()
            this.data[attribute] = value
        }

        override fun <T : Any, A> get(attribute: A): T?
                where A : EntityAttributeDescriptor<T, *>,
                      A : EntityAttributeDescriptor._Optional<T, *> {
            @Suppress("UNCHECKED_CAST")
            return this.data[attribute] as T?
        }

        override fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T {
            @Suppress("UNCHECKED_CAST")
            return this.data[attribute] as T
        }
    }

    override fun rollback() {
        this.changes.clear()
        for (o in this.changes) {
            val cp = LocalEntity(true)
            o.copyInto(cp)
            cp.isMutable = false
            this.changes.add(cp)
        }
    }

    override fun clear() {
        this.changes.clear()
    }

    private inner class EntityCreatorImpl private constructor(
        private val data: LocalEntity
    ) : EntityCreationTransaction, EntityAccessor by data, EntityMutator by data {
        constructor() : this(this@FullRewriteLocalCopyStorage.LocalEntity(true))

        private var isActive = true

        @Suppress("NOTHING_TO_INLINE", "FunctionName")
        private inline fun _assertIsActive() {
            if (!this.isActive) throw IllegalStateException("Creating transaction is closed")
        }

        override fun cancelCreating() {
            this.isActive = false
            this.data.isMutable = false
            this@FullRewriteLocalCopyStorage.globalMutex.unlock()
        }

        @Suppress("FunctionName")
        suspend fun _finishCreating() {
            this.data.isMutable = false
            // todo assert all attrs
            this@FullRewriteLocalCopyStorage.changes.add(this.data)
        }

        override suspend fun finishCreating() =
            this@FullRewriteLocalCopyStorage.globalMutex.withLock(this) {
                this._finishCreating()
            }

        override suspend fun finishCreatingIfMin(): Boolean =
            this@FullRewriteLocalCopyStorage.globalMutex.withLock(this) {
                val f = this@FullRewriteLocalCopyStorage.changes
                    .all { o -> this@FullRewriteLocalCopyStorage.rootEntityDescriptor.compare(this, o) < 0 }
                if (f)
                    this._finishCreating()
                return f
            }

        override suspend fun finishCreatingIfMax(): Boolean =
            this@FullRewriteLocalCopyStorage.globalMutex.withLock(this) {
                val f = this@FullRewriteLocalCopyStorage.changes
                    .all { o -> this@FullRewriteLocalCopyStorage.rootEntityDescriptor.compare(this, o) > 0 }
                if (f)
                    this._finishCreating()
                return f
            }

        override val descriptor: EntityDescriptor
            get() = this@FullRewriteLocalCopyStorage.rootEntityDescriptor

        override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute): EntityMutator {
            this._assertIsActive()
            return this.data[attribute]
        }

        override fun <T : Any> set(attribute: EntityAttributeDescriptor<T, *>, value: T) {
            this._assertIsActive()
            this.data[attribute] = value
        }

        override fun <T : Any, A> set(attribute: A, value: T?)
                where A : EntityAttributeDescriptor<T, *>,
                      A : EntityAttributeDescriptor._Optional<T, *> {
            this._assertIsActive()
            this.data[attribute] = value
        }

        override fun set(attribute: EntityAttributeDescriptor.ComplexAttribute.Optional, value: Nothing?) {
            this._assertIsActive()
            this.data[attribute] = value
        }
    }

    override fun startEntityCreating(): EntityCreationTransaction =
        this.EntityCreatorImpl()


    private inner class ActionTransactionImpl(
        private val mutexOwner: Any,
        private val collected: Map<Int, LocalEntity>
    ) : ActionTransaction {
        private var isActive = true

        @Suppress("FunctionName")
        private fun _assertActive() {
            if (!this.isActive) throw IllegalStateException("This transaction was closed")
        }

        @Suppress("FunctionName")
        private fun _unlock() {
            try {
                this@FullRewriteLocalCopyStorage.globalMutex.unlock(this.mutexOwner)
            } catch (e1: IllegalStateException) {
                throw IllegalStateException("This transaction was closed", e1)
            }
        }

        override suspend fun delete() {
            this._assertActive()
            try {
                for (i in collected.keys.sorted().reversed())
                    this@FullRewriteLocalCopyStorage.changes.removeAt(i)
            } finally {
                this.isActive = false
                this._unlock()
            }
        }

        private inner class SelectorImpl : StorageEntityReader {
            private val iterator = this@ActionTransactionImpl.collected.values.iterator()
            private var current: EntityAccessor? = null
                set(value) {
                    field = value
                    this.currentWrapped = if (value == null) NullEntityAccessor else this.SafeAccessor(value, value)
                }

            @Suppress("PrivatePropertyName")
            private val NullEntityAccessor = object : EntityAccessor {
                fun `throw`(): Nothing = throw IllegalStateException("Entity not fetched yet")
                override val descriptor: EntityDescriptor
                    get() = this.`throw`()

                override fun <T : Any, A> get(attribute: A): T?
                        where A : EntityAttributeDescriptor<T, *>,
                              A : EntityAttributeDescriptor._Optional<T, *> {
                    this.`throw`()
                }

                override fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T {
                    this.`throw`()
                }

            }

            private var currentWrapped: EntityAccessor = NullEntityAccessor
                get() = if (this.current != null) field else NullEntityAccessor.`throw`()

            private var isActive = true

            @Suppress("FunctionName")
            private fun _assertActive() {
                if (!this.isActive)
                    throw IllegalStateException("All entities already fetched or transaction aborted")
            }

            override suspend fun next(): Boolean {
                this._assertActive()
                if (!this.iterator.hasNext()) {
                    this.current = null
                    this.isActive = false
                    this@ActionTransactionImpl._unlock()
                    return false
                }
                this.current = this.iterator.next()
                return true
            }

            override suspend fun abortFetching() {
                this._assertActive()
                this.current = null
                this.isActive = false
                this@ActionTransactionImpl._unlock()
            }

            override val descriptor: EntityDescriptor
                get() = this@FullRewriteLocalCopyStorage.rootEntityDescriptor

            private inner class SafeAccessor(
                private val key: EntityAccessor,
                private val target: EntityAccessor
            ) : EntityAccessor {

                @OptIn(ExperimentalContracts::class)
                @Suppress("FunctionName")
                private inline fun <T> _assertActive(action: () -> T): T {
                    contract {
                        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
                    }
                    if (this.key !== this@SelectorImpl.current)
                        throw IllegalStateException("This accessor is unbound")
                    return action()
                }

                override val descriptor: EntityDescriptor
                    get() = this._assertActive { this.target.descriptor }

                override fun <T : Any, A> get(attribute: A): T?
                        where A : EntityAttributeDescriptor<T, *>,
                              A : EntityAttributeDescriptor._Optional<T, *> = this._assertActive {
                    @Suppress("UNCHECKED_CAST")
                    if (attribute is EntityAttributeDescriptor.ComplexAttribute)
                        return@_assertActive this.target[attribute]
                            ?.let { e -> this@SelectorImpl.SafeAccessor(this.key, e as EntityAccessor) } as T?
                    else
                        return@_assertActive this.target[attribute]
                }

                override fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T = this._assertActive {
                    @Suppress("UNCHECKED_CAST")
                    if (attribute is EntityAttributeDescriptor.ComplexAttribute)
                        return@_assertActive this.target[attribute]
                            .let { e -> this@SelectorImpl.SafeAccessor(this.key, e) } as T
                    else
                        return@_assertActive this.target[attribute]
                }

            }

            override fun <T : Any, A> get(attribute: A): T?
                    where A : EntityAttributeDescriptor<T, *>,
                          A : EntityAttributeDescriptor._Optional<T, *> {
                this._assertActive()
                return this.currentWrapped[attribute]
            }

            override fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T {
                this._assertActive()
                return this.currentWrapped[attribute]
            }

        }

        override suspend fun select(vararg attributes: EntityAttributeDescriptor<*, *>): StorageEntityReader {
            this._assertActive()
            this.isActive = false
            return this.SelectorImpl()
        }

        private inner class UpdaterImpl : StorageEntityUpdater {
            private var isActive = false
            private val newEntities = this@ActionTransactionImpl.collected.mapValues { (k, v) ->
                val e = EntityMapImpl(this@FullRewriteLocalCopyStorage.rootEntityDescriptor)
                v.copyInto(e)
                return@mapValues e
            }

            private inner class RecursiveUpdater(
                val parent: RecursiveUpdater?,
                val attribute: EntityAttributeDescriptor.ComplexAttribute
            ) : EntityMutator {
                @Suppress("FunctionName")
                private fun _getMutator(root: EntityMutator): EntityMutator? {
                    @Suppress("LiftReturnOrAssignment")
                    if (this.attribute is EntityAttributeDescriptor.ComplexAttribute.Optional) {
                        if (this.parent == null)
                            return root[this.attribute]
                        else
                            return this.parent._getMutator(root)?.get(this.attribute)
                    } else {
                        if (this.parent == null)
                            return root[this.attribute]
                        else
                            return this.parent._getMutator(root)?.get(this.attribute)
                    }
                }

                override val descriptor: EntityDescriptor
                    get() = this@UpdaterImpl._assertActive { this.attribute.targetEntity }

                private val _cache = HashMap<EntityAttributeDescriptor.ComplexAttribute, RecursiveUpdater>()

                override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute): EntityMutator =
                    this@UpdaterImpl._assertActive {
                        this._cache.getOrPut(attribute) { RecursiveUpdater(this, attribute) }
                    }

                override fun set(attribute: EntityAttributeDescriptor.ComplexAttribute.Optional, value: Nothing?): Unit =
                    this@UpdaterImpl._assertActive {
                        this@UpdaterImpl.newEntities.values.onEach { e ->
                            this._getMutator(e)?.set(attribute, null)
                        }
                    }


                override fun <T : Any, A> set(attribute: A, value: T?): Unit
                        where A : EntityAttributeDescriptor<T, *>,
                              A : EntityAttributeDescriptor._Optional<T, *> =
                    this@UpdaterImpl._assertActive {
                        this@UpdaterImpl.newEntities.values.onEach { e ->
                            this._getMutator(e)?.set(attribute, value)
                        }
                    }

                override fun <T : Any> set(attribute: EntityAttributeDescriptor<T, *>, value: T): Unit =
                    this@UpdaterImpl._assertActive {
                        this@UpdaterImpl.newEntities.values.onEach { e ->
                            this._getMutator(e)?.set(attribute, value)
                        }
                    }
            }

            private val cache = HashMap<EntityAttributeDescriptor.ComplexAttribute, RecursiveUpdater>()

            @OptIn(ExperimentalContracts::class)
            @Suppress("FunctionName")
            private inline fun <T> _assertActive(
                onActive: () -> T
            ): T {
                contract {
                    callsInPlace(onActive, InvocationKind.AT_MOST_ONCE)
                }
                if (!this.isActive)
                    throw IllegalStateException("This updater is inactive")
                return onActive()
            }

            override val descriptor: EntityDescriptor
                get() = this._assertActive { this@FullRewriteLocalCopyStorage.rootEntityDescriptor }

            override fun get(attribute: EntityAttributeDescriptor.ComplexAttribute): EntityMutator =
                this._assertActive { this.cache.getOrPut(attribute) { RecursiveUpdater(null, attribute) } }

            override fun set(attribute: EntityAttributeDescriptor.ComplexAttribute.Optional, value: Nothing?) = this._assertActive {
                this.newEntities.values.forEach { e ->
                    e[attribute] = null
                }
            }

            override fun <T : Any, A> set(attribute: A, value: T?)
                    where A : EntityAttributeDescriptor<T, *>,
                          A : EntityAttributeDescriptor._Optional<T, *> = this._assertActive {
                this.newEntities.values.forEach { e ->
                    e[attribute] = value
                }
            }

            override fun <T : Any> set(attribute: EntityAttributeDescriptor<T, *>, value: T) =
                this._assertActive {
                    this.newEntities.values.forEach { e ->
                        e[attribute] = value
                    }
                }

            override suspend fun performUpdate(): Unit =
                this._assertActive {
                    this.newEntities.forEach { (k, v) ->
                        this@FullRewriteLocalCopyStorage.changes[k] = v
                    }
                    this.isActive = false
                    this@ActionTransactionImpl._unlock()
                }

            override suspend fun abort() = this._assertActive {
                this.isActive = false
                this@ActionTransactionImpl._unlock()
            }
        }

        override suspend fun update(): StorageEntityUpdater {
            this._assertActive()
            this.isActive = false
            return this.UpdaterImpl()
        }

        override suspend fun count(): UInt {
            this._assertActive()
            try {
                return this.collected.size.toUInt()
            } finally {
                this.isActive = false
                this._unlock()
            }
        }

        override suspend fun abort() {
            this._assertActive()
            this._unlock()
        }
    }

    @Suppress("FunctionName")
    private fun _formatIndex2EntityMap() = buildMap map@{
        this@FullRewriteLocalCopyStorage._changes.forEachIndexed { i, e ->
            this@map[i] = e
        }
    }

    override suspend fun startActionByFilter(filter: Filter): ActionTransaction {
        val mutexOwner = Any()
        this.globalMutex.lock(mutexOwner)
        var collected: Map<Int, LocalEntity>? = null
        try {
            for (constraint in filter) {
                when (constraint) {
                    Filter.Action.All ->
                        collected = collected ?: this._formatIndex2EntityMap()

                    is Filter.Action.CompareAttribute<*> ->
                        collected = (collected ?: this._formatIndex2EntityMap()).filterValues { v ->
                            when (constraint.direction) {
                                Filter.Action.ComparatorDirection.LOWER -> constraint.compare(v) < 0
                                Filter.Action.ComparatorDirection.EQUAL -> constraint.compare(v) == 0
                                Filter.Action.ComparatorDirection.GREATER -> constraint.compare(v) > 0
                            }
                        }

                    is Filter.Action.CompareEntity ->
                        collected = (collected ?: this._formatIndex2EntityMap()).filterValues { v ->
                            when (constraint.direction) {
                                Filter.Action.ComparatorDirection.LOWER -> this.rootEntityDescriptor.compare(v, constraint.targetEntity) < 0
                                Filter.Action.ComparatorDirection.EQUAL -> this.rootEntityDescriptor.compare(v, constraint.targetEntity) == 0
                                Filter.Action.ComparatorDirection.GREATER -> this.rootEntityDescriptor.compare(v, constraint.targetEntity) > 0
                            }
                        }

                    Filter.Action.FirstOnly -> collected = (collected ?: this._formatIndex2EntityMap()).let { c ->
                        val minKey = c.keys.minOrNull() ?: return@let emptyMap()
                        return@let mapOf(minKey to c[minKey]!!)
                    }
                }
            }
            return this.ActionTransactionImpl(mutexOwner, collected ?: throw IllegalArgumentException("Filter selects nothing"))
        } catch (e: Throwable) {
            this.globalMutex.unlock(mutexOwner)
            throw e
        }
        }

    private inner class GroupCounterImpl<T : Any>(
        private val mutexOwner: Any,
        override val attribute: EntityAttributeDescriptor<T, *>,
        override val nullCount: UInt,
        private val groups: Iterator<Map.Entry<T, UInt>>
    ) : CountByGroupFetcher<T> {

        @Suppress("FunctionName")
        private fun _throwNotFetched(): Nothing = throw IllegalStateException("No one group was fetched")

        @Suppress("FunctionName")
        private fun _throwClosed(): Nothing = throw IllegalStateException("All groups already returned")

        @Suppress("PrivatePropertyName")
        private val StartingEntry = object : Map.Entry<T, UInt> {
            override val key: T
                get() = this@GroupCounterImpl._throwNotFetched()
            override val value: UInt
                get() = this@GroupCounterImpl._throwNotFetched()

        }

        private var current: Map.Entry<T, UInt>? = this.StartingEntry

        override val groupValue: T
            get() = when (val c = this.current) {
                this.StartingEntry -> this._throwNotFetched()
                null -> this._throwClosed()
                else -> c.key
            }
        override val count: UInt
            get() = when (val c = this.current) {
                this.StartingEntry -> this._throwNotFetched()
                null -> this._throwClosed()
                else -> c.value
            }

        @Suppress("FunctionName")
        private fun _unlock() {
            try {
                this@FullRewriteLocalCopyStorage.globalMutex.unlock(this.mutexOwner)
            } catch (e: IllegalStateException) {
                throw IllegalStateException("All groups already returned", e)
            }
        }

        override suspend fun next(): Boolean {
            if (this.current == null) this._throwClosed()
            if (!this.groups.hasNext()) {
                this.current = null
                this._unlock()
                return false
            }
            this.current = this.groups.next()
            return true
        }

        override suspend fun abortFetching() {
            if (this.current == null) this._throwClosed()
            this.current = null
            this._unlock()
        }

    }

    override suspend fun <T : Any> countByGroup(attr: EntityAttributeDescriptor<T, *>): CountByGroupFetcher<T> {
        val mutexOwner = Any()
        var nullsCount = 0u
        val groups = HashMap<T, UInt>()
        this.globalMutex.lock(mutexOwner)
        try {
            this.changes.forEach { e ->
                val v = e[attr]
                if (v == null)
                    nullsCount++
                else
                    groups[v] = groups.getOrElse(v) { 0u } + 1u
            }
            return this.GroupCounterImpl(mutexOwner, attr, nullsCount, groups.entries.iterator())
        } catch (e: Throwable) {
            this.globalMutex.unlock(mutexOwner)
            throw e
        }
    }

    override suspend fun shuffleInline() = this.globalMutex.withLock {
        this.changes.shuffle()
    }

    override suspend fun reverseInline() = this.globalMutex.withLock {
        this.changes.reverse()
    }
}