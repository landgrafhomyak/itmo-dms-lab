package ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle

object AnsiStylesLightTheme: AnsiStyles {
    override fun get(style: ConsoleTextStyle): String = when(style) {
        ConsoleTextStyle.DEFAULT -> "\u001B[40m\u001B[24m"
        ConsoleTextStyle.UTILITY -> "\u001B[90m\u001B[24m"
        ConsoleTextStyle.INPUT -> "\u001B[32m\u001B[24m"
        ConsoleTextStyle.ERROR -> "\u001B[31m\u001B[24m"
        ConsoleTextStyle.HIGHLIGHT -> "\u001B[35m\u001B[4m"
        ConsoleTextStyle.TIP -> "\u001B[94m\u001B[24m"
    }
}