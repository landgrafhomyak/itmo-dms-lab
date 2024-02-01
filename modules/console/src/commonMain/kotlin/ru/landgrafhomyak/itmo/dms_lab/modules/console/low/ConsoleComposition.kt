package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

class ConsoleComposition(
    private val input: InputConsoleInterface,
    private val output: OutputConsoleInterface
): ConsoleInterface, InputConsoleInterface by input, OutputConsoleInterface by output