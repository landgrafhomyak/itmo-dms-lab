package ru.landgrafhomyak.itmo.dms_lab.modules.cli_args_parser

interface CliArgsParserLogger {
    fun fetched(descriptor: ArgumentDescriptor<*>, value: String?)
    fun error(descriptor: ArgumentDescriptor<*>, value: String?, errorMessage: String)
    fun warning(descriptor: ArgumentDescriptor<*>, value: String?, errorMessage: String)
}