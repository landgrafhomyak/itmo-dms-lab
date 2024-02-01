package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

class ConsoleFromMemory(
    private val inputs: Iterator<String>,
) : InputConsoleInterface {
    constructor(
        inputs: Iterable<String>,
    ) : this(inputs.iterator())

    constructor(
        printer: ConsoleInterface,
        vararg inputs: String
    ) : this(inputs.iterator())

    override fun readln(): String? =
        if (!this.inputs.hasNext()) null
        else this.inputs.next()

}