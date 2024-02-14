package ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.OutputConsoleInterface

class AnsiColorsOutputConsoleWrapper(
    private val styles: AnsiStyles,
    private val raw: OutputConsoleInterface
) : OutputConsoleInterface by raw {
    override suspend fun setStyle(style: ConsoleTextStyle) {
        this.raw.print(this.styles[style])
    }
}