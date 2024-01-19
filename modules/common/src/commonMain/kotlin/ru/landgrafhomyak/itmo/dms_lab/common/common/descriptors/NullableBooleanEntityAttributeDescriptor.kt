package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors

@Suppress("ConvertObjectToDataObject")
object NullableBooleanEntityAttributeDescriptor : EntityAttributeDescriptor.BooleanAttribute {
    override val isNullable: Boolean get() = true
}