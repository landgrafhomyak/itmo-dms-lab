package ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle

class AnsiStylesMapImpl(private val map: Map<ConsoleTextStyle, String>) : AnsiStyles {
    override fun get(style: ConsoleTextStyle): String = this.map[style] ?: "\u001b[0m"
}