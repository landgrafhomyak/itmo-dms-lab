package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMapImpl
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.Filter
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.FilterBuilder

class CommonFilterBuilderImpl(
    private val rootEntityDescriptor: EntityDescriptor
) : FilterBuilder {
    private var collectedActions = ArrayList<Filter.Action>()
    override fun all(): FilterBuilder {
        this.collectedActions.add(Filter.Action.All)
        return this
    }

    override fun <T : Comparable<T>> filterLower(attr: EntityAttributeDescriptor<T, *>, value: T): FilterBuilder {
        this.collectedActions.add(
            Filter.Action.CompareAttribute(
                Filter.Action.ComparatorDirection.LOWER,
                attr,
                value
            )
        )
        return this
    }

    override fun <T : Comparable<T>> filterEqual(attr: EntityAttributeDescriptor<T, *>, value: T?): FilterBuilder {
        if (value == null) {
            this.collectedActions.add(
                Filter.Action.CompareAttributeNull(attr)
            )
        } else {
            this.collectedActions.add(
                Filter.Action.CompareAttribute(
                    Filter.Action.ComparatorDirection.EQUAL,
                    attr,
                    value
                )
            )
        }
        return this
    }

    override fun <T : Comparable<T>> filterGreater(attr: EntityAttributeDescriptor<T, *>, value: T): FilterBuilder {
        this.collectedActions.add(
            Filter.Action.CompareAttribute(
                Filter.Action.ComparatorDirection.GREATER,
                attr,
                value
            )
        )
        return this
    }

    override fun filterLower(than: EntityAccessor): FilterBuilder {
        val wrapper = EntityMapImpl(this.rootEntityDescriptor, HashMap())
        than.copyInto(wrapper)
        this.collectedActions.add(
            Filter.Action.CompareEntity(
                Filter.Action.ComparatorDirection.LOWER,
                wrapper
            )
        )
        return this
    }

    override fun filterGreater(than: EntityAccessor): FilterBuilder {
        val wrapper = EntityMapImpl(this.rootEntityDescriptor, HashMap())
        than.copyInto(wrapper)
        this.collectedActions.add(
            Filter.Action.CompareEntity(
                Filter.Action.ComparatorDirection.GREATER,
                wrapper
            )
        )
        return this
    }

    override fun firstOnly(): FilterBuilder {
        this.collectedActions.add(Filter.Action.FirstOnly)
        return this
    }

    override fun build(): Filter {
        return CommonFilterImpl(this.rootEntityDescriptor, ArrayList(this.collectedActions))
    }
}