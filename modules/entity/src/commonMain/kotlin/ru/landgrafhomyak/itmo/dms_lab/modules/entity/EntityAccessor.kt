package ru.landgrafhomyak.itmo.dms_lab.modules.entity

import kotlin.jvm.JvmName

interface EntityAccessor {
    val descriptor: EntityDescriptor

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("getOptional")
    operator fun <T : Any, A> get(attribute: A): T?
            where A : EntityAttributeDescriptor<T, *>,
                  A : EntityAttributeDescriptor._Optional<T, *>

    operator fun <T : Any> get(attribute: EntityAttributeDescriptor<T, *>): T

    fun copyInto(dst: EntityMutator) {
        for (attr in this.descriptor) {
            when (attr) {
                is EntityAttributeDescriptor.ComplexAttribute.Optional -> {
                    val v = this[attr]
                    if (v == null)
                        dst[attr] = null
                    else
                        v.copyInto(dst[attr])
                }

                is EntityAttributeDescriptor.ComplexAttribute -> {
                    this[attr].copyInto(dst[attr])
                }

                else -> {
                    if (attr is EntityAttributeDescriptor._Optional<*, *>)
                        dst[attr] = this[attr]
                    else
                        dst[attr] = this[attr]
                }
            }
        }
    }
}