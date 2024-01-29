package ru.landgrafhomyak.itmo.dms_lab.utility

data class IPv4 constructor(
    private val b1: UByte,
    private val b2: UByte,
    private val b3: UByte,
    private val b4: UByte,
    val port: UShort? = null
) {
    val addressAsString: String
        get() = "${this.b1}.${this.b2}.${this.b3}.${this.b4}"

    companion object {
        private val pattern = Regex("""^(\d+)\.(\d+)\.(\d+)\.(\d+)(?::(\d+))?$""")

        /**
         * @throws IllegalArgumentException if passed value is not convertable to IP v4.
         */
        @JvmStatic
        fun parseFromString(s: String): IPv4 {
            val m = this.pattern.matchEntire(s) ?: throw IllegalArgumentException("Invalid IP v4 format")
            return IPv4(
                m.groupValues[1].toUByte(),
                m.groupValues[2].toUByte(),
                m.groupValues[3].toUByte(),
                m.groupValues[4].toUByte(),
                m.groups[5]?.value?.toUShort()
            )
        }
    }
}