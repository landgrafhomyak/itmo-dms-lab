package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors

class RangedIntEntityAttributeDescriptor(
    private val minValue: Long,
    private val maxValue: Long,
    override val isNullable: Boolean = false
) : EntityAttributeDescriptor.IntAttribute {
    override fun checkValid(value: Long): Boolean {
        return this.minValue <= value && value <= this.maxValue
    }
}