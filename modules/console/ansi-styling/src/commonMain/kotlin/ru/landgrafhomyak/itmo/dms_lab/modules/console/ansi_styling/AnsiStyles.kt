package ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle

interface AnsiStyles {
    operator fun get(style:ConsoleTextStyle): String
}