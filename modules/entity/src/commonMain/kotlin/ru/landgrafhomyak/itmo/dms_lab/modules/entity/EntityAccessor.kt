package ru.landgrafhomyak.itmo.dms_lab.modules.entity

import kotlin.jvm.JvmName

interface EntityAccessor {
    val descriptor: EntityDescriptor

    operator fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T?

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("getRequired")
    operator fun <T : Any, A> get(attribute: A): T
            where A : EntityAttributeDescriptor<T, *>,
                  A : EntityAttributeDescriptor._Required<T, *>

    fun copyInto(dst: EntityMutator) {
        for (attr in this.descriptor) {
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            when (attr) {

                is EntityAttributeDescriptor.ComplexAttribute -> {
                    this[attr].copyInto(dst[attr])
                }

                is EntityAttributeDescriptor._Optional<*, *> -> {
                    dst[attr] = this[attr]

                }

                is EntityAttributeDescriptor._Required<*, *> -> {
                    dst[attr] = this[attr]
                }

                else -> throw RuntimeException("Unknown attribute type")
            }
        }
    }
}