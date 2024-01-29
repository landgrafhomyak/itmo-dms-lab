package ru.landgrafhomyak.itmo.dms_lab.modules.cli_args_parser

open class ArgumentDescriptorsSet(vararg args: ArgumentDescriptor<Any>) {
    // don't use .associateBy{ad->ad.keyword} to format verbose error description
    private val map: Map<String, ArgumentDescriptor<Any>> = buildMap {
        val duplications = HashSet<String>()
        for (ad in args)
            this@buildMap.put(ad.keyword, ad)?.keyword?.also(duplications::add)
        if (duplications.isNotEmpty())
            throw IllegalArgumentException("CLI arguments duplication: ${duplications.joinToString { s -> "'$s'" }}")
    }

    @JvmName("getDescriptorByKeyword")
    operator fun get(keyword: String): ArgumentDescriptor<Any>? = this.map[keyword]

    @JvmName("containsDescriptor")
    operator fun contains(descriptor: ArgumentDescriptor<*>): Boolean = this[descriptor.keyword] === descriptor
}