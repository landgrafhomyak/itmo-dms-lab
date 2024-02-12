package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.OutputConsoleInterface

class PrintOnInputConsoleWrapper(
    private val input: InputConsoleInterface,
    private val output: OutputConsoleInterface,
) : ConsoleInterface, OutputConsoleInterface by output {
    constructor(console: ConsoleInterface) : this(console, console)

    private var lastStyle = ConsoleTextStyle.DEFAULT
    override suspend fun readln(): String? {
        val line = this.input.readln() ?: return null
        this.output.setStyle(ConsoleTextStyle.INPUT)
        this.output.println(line)
        this.output.setStyle(this.lastStyle)
        return line
    }

    override suspend fun setStyle(style: ConsoleTextStyle) {
        this.lastStyle = style
        this.output.setStyle(style)
    }
}