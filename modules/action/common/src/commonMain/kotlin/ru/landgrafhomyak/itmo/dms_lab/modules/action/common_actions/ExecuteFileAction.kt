package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action
import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrintOnInputConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.engine.DefaultConsoleEngine
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object ExecuteFileAction : Action {
    override val name: String
        get() = "execute"
    override val description: String
        get() = "Executes file as script"

    override suspend fun executeIO(storage: StorageClientLayer, io: ActionIoProvider, environment: Environment) {
        val path = io.readln()
        if (io.finishArgsReading()) return
        if (path == null) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Path to file not set")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            return
        }
        val recMeta: Pair<Environment, InputConsoleInterface?>
        try {
            recMeta = environment.fileStream(path)
        } catch (t: Throwable) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Unable to access file")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            throw t
        }
        val recEnvironment = recMeta.first
        val fileInput = recMeta.second
        if (fileInput == null) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Specified file is a directory")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            return
        }

        val recConsole = PrintOnInputConsoleWrapper(fileInput, io)
        val recEngine = DefaultConsoleEngine(recConsole, recEnvironment, storage)
        recEngine.mainloop()
    }
}