package ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors

class RangedFloatEntityAttributeDescriptor(
    private val minValue: Double,
    private val maxValue: Double,
    override val isNullable: Boolean = false
) : EntityAttributeDescriptor.FloatAttribute {
    override fun checkValid(value: Double): Boolean {
        return this.minValue <= value && value <= this.maxValue
    }
}