package ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle

class AnsiColorsConsoleWrapper(
    private val styles: AnsiStyles,
    private val raw: ConsoleInterface
): ConsoleInterface by raw {
    override suspend fun setStyle(style: ConsoleTextStyle) {
        this.raw.print(this.styles[style])
    }
}