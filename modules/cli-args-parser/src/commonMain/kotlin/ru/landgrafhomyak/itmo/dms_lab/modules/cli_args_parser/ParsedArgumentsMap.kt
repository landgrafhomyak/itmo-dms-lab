package ru.landgrafhomyak.itmo.dms_lab.modules.cli_args_parser

class ParsedArgumentsMap internal constructor(
    val set: ArgumentDescriptorsSet,
    private val unique: Map<ArgumentDescriptor._Unique<*>, Any>,
    private val repeatable: Map<ArgumentDescriptor._Repeatable<*>, Collection<Any>>,
    val freeArgs: Collection<String>
) {
    operator fun <T : Any> get(argumentDescriptor: ArgumentDescriptor._Unique<T>): T? {
        require(argumentDescriptor in this.set) { "Provided descriptor wasn't expected" }
        @Suppress("UNCHECKED_CAST")
        return this.unique[argumentDescriptor] as T?
    }

    operator fun <T : Any> get(argumentDescriptor: ArgumentDescriptor._Repeatable<T>): Collection<T>? {
        require(argumentDescriptor in this.set) { "Provided descriptor wasn't expected" }
        @Suppress("UNCHECKED_CAST")
        return this.repeatable[argumentDescriptor] as Collection<T>?
    }
}