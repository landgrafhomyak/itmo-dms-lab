package ru.landgrafhomyak.itmo.dms_lab.modules.command

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface

interface ConsoleCommandEnvironment {
    /**
     * Parses path (relative to directory to which this environment bound or absolute) and
     * returns environment for the resulting directory and prepared history and input stream
     * ***if*** a path points to file.
     */
    fun fileStream(relative: String): Pair<ConsoleCommandEnvironment, InputConsoleInterface?>

    val commandsSet: ConsoleCommandsSet

    val login: String?

    fun addCommandToHistory(command: ConsoleCommand)

    val commandHistory: Iterable<ConsoleCommand>
}