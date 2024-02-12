package ru.landgrafhomyak.itmo.dms_lab.modules.action

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface

interface Environment {
    /**
     * Parses path (relative to directory to which this environment bound or absolute) and
     * returns environment for the resulting directory (may return this instance if the
     * directory is same) and input stream ***if*** a path points to file.
     */
    fun fileStream(relative: String): Pair<Environment, InputConsoleInterface?>

    val actionsSet: ActionsSet

    val login: String?
}