package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.local_copy

import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAccessor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityDescriptor

class DefaultFullRewriteLocalCopyStorage(
    rootEntityDescriptor: EntityDescriptor
) : FullRewriteLocalCopyStorage(rootEntityDescriptor) {
    override val checkpoint: MutableList<EntityAccessor> = ArrayList()
    override val changes: MutableList<EntityAccessor> = ArrayList()
}