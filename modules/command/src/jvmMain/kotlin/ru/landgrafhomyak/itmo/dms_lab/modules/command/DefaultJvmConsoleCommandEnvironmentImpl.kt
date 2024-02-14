package ru.landgrafhomyak.itmo.dms_lab.modules.command

import ru.landgrafhomyak.itmo.dms_lab.modules.console.InplaceScannerInputConsole
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface
import java.nio.file.Path
import java.util.LinkedList
import kotlin.io.path.isDirectory

class DefaultJvmConsoleCommandEnvironmentImpl(
    private val cwd: Path,
    override val commandsSet: ConsoleCommandsSet,
    override val login: String?,
    private val historyLimit: UInt = 10u
) : ConsoleCommandEnvironment {
    override fun fileStream(relative: String): Pair<ConsoleCommandEnvironment, InputConsoleInterface?> {
        val newPath = this.cwd.resolve(relative)
        if (newPath.isDirectory())
            return DefaultJvmConsoleCommandEnvironmentImpl(newPath, this.commandsSet, this.login) to null
        val stream = InplaceScannerInputConsole(newPath)
        val env = DefaultJvmConsoleCommandEnvironmentImpl(newPath.parent, this.commandsSet, this.login)
        return env to stream
    }

    private var history = LinkedList<ConsoleCommand>()

    override fun addCommandToHistory(command: ConsoleCommand) {
        if (this.history.size.toUInt() >= this.historyLimit)
            this.history.removeFirst()
        this.history.add(command)
    }

    override val commandHistory: Iterable<ConsoleCommand>
        get() = this.history
}