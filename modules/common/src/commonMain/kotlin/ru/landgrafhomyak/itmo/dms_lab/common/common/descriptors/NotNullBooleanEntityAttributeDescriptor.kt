package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors

@Suppress("ConvertObjectToDataObject")
object NotNullBooleanEntityAttributeDescriptor : EntityAttributeDescriptor.BooleanAttribute {
    override val isNullable: Boolean get() = false
}