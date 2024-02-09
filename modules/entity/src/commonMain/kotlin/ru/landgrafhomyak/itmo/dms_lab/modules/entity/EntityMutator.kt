package ru.landgrafhomyak.itmo.dms_lab.modules.entity

import kotlin.jvm.JvmName

interface EntityMutator {
    val descriptor: EntityDescriptor
    operator fun get(attribute: EntityAttributeDescriptor.ComplexAttribute): EntityMutator

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("setOptional")
    operator fun <T : Any, A> set(attribute: A, value: T?)
            where A : EntityAttributeDescriptor<T, *>,
                  A : EntityAttributeDescriptor._Optional<T, *>

    operator fun <T : Any> set(attribute: EntityAttributeDescriptor<T, *>, value: T)
}