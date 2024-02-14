package ru.landgrafhomyak.itmo.dms_lab.apps.demo.lab5

import kotlinx.coroutines.runBlocking
import ru.landgrafhomyak.itmo.dms_lab.models.demo.DemoRootEntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandsSetMapImpl
import ru.landgrafhomyak.itmo.dms_lab.modules.command.DefaultJvmConsoleCommandEnvironmentImpl
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.AddEntityCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.AddEntityIfMaxCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.AddEntityIfMinCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.ClearCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.ExecuteFileCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.ExitCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.HistoryCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.ListCommandsCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.RemoveFirstCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.RemoveGreaterCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.RemoveHeadCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.RemoveLastCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.RemoveLowerCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.RemoveTailCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.ReverseCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.universal_commands.ShuffleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.console.DefaultConsoleEngine
import ru.landgrafhomyak.itmo.dms_lab.modules.console.QueuedPlainStdioConsole
import ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling.AnsiColorsConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.console.ansi_styling.AnsiStylesLightTheme
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.local_copy.DefaultFullRewriteLocalCopyStorage
import kotlin.io.path.Path

object Main {
    @JvmStatic
    fun main(arv: Array<String>) {
        val commands = ConsoleCommandsSetMapImpl(
                    AddEntityCommand,
                    AddEntityIfMaxCommand,
                    AddEntityIfMinCommand,
                    ClearCommand,
                    ExecuteFileCommand,
                    ExitCommand,
                    HistoryCommand,
                    ListCommandsCommand(),
                    RemoveFirstCommand,
                    RemoveGreaterCommand(DemoRootEntityDescriptor),
                    RemoveHeadCommand,
                    RemoveLastCommand,
                    RemoveLowerCommand(DemoRootEntityDescriptor),
                    RemoveTailCommand,
                    ReverseCommand,
                    ShuffleCommand
        )


        val storage = DefaultFullRewriteLocalCopyStorage(DemoRootEntityDescriptor)
        val ioBlocking = QueuedPlainStdioConsole()
        val ioThread = Thread { ioBlocking.serverForever() }
        ioThread.isDaemon = true
        ioThread.start()
        val io = AnsiColorsConsoleWrapper(AnsiStylesLightTheme, ioBlocking)
        val env = DefaultJvmConsoleCommandEnvironmentImpl(Path("."), commands, null)
        val consoleEngine = DefaultConsoleEngine(io, env, storage)
        runBlocking {
            consoleEngine.serveForever()
        }
    }
}