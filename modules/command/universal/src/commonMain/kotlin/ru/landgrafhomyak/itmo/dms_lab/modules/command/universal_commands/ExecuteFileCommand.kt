package ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.DefaultConsoleEngine
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrintOnInputConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

object ExecuteFileCommand : ConsoleCommand {
    override val name: String
        get() = "execute"
    override val description: String
        get() = "Executes file as script"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        val path = io.argsOrNull
        if (path == null) {
            io.setStyle(ConsoleTextStyle.ERROR)
            io.println("Path to file not set")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            return
        }
        val recMeta: Pair<ConsoleCommandEnvironment, InputConsoleInterface?>
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