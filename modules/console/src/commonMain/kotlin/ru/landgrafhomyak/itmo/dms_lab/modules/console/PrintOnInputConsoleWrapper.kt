package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.OutputConsoleInterface

class PrintOnInputConsoleWrapper(private val stream: ConsoleInterface) : ConsoleInterface, OutputConsoleInterface by stream {
    private var lastStyle = ConsoleTextStyle.DEFAULT
    override suspend fun readln(): String? {
        val line = this.stream.readln() ?: return null
        this.stream.setStyle(ConsoleTextStyle.INPUT)
        this.stream.println(line)
        this.stream.setStyle(this.lastStyle)
        return line
    }

    override suspend fun setStyle(style: ConsoleTextStyle) {
        this.lastStyle = style
        this.stream.setStyle(style)
    }
}