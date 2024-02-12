package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.OutputConsoleInterface

class ConsoleComposition(
    private val input: InputConsoleInterface,
    private val output: OutputConsoleInterface
): ConsoleInterface, InputConsoleInterface by input, OutputConsoleInterface by output