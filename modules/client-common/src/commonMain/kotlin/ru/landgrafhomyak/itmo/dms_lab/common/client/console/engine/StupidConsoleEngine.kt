package ru.landgrafhomyak.itmo.dms_lab.common.client.console.engine

import ru.landgrafhomyak.itmo.dms_lab.common.client.console.io.ConsoleColor
import ru.landgrafhomyak.itmo.dms_lab.common.client.console.io.ConsoleInterface

class StupidConsoleEngine(private val console: ConsoleInterface, private val commandsContext: CommandsContext) {
    @Suppress("SpellCheckingInspection")
    fun mainloop() {
        while (true) {
            this.console.setUnderline(false)
            this.console.setColor(ConsoleColor.GREY)
            this.console.print("> ")
            this.console.setColor(ConsoleColor.DEFAULT)
            val rawCommand = this.console.readln()
            if (rawCommand == null) {
                this.console.setUnderline(false)
                this.console.setColor(ConsoleColor.GREY)
                this.console.println("\nInput stream ended, exiting")
                this.console.setColor(ConsoleColor.DEFAULT)
                return
            }
            val args = ArgsParser.parseToList(rawCommand)
            if (args.isEmpty())
                continue
            val command = args[0]
            val entityCreator =  this.commandsContext.isEntityCommand(command)
            if (entityCreator == null)
            {
                this.commandsContext.executeCommand(this.console, command, args.subList(1, args.size))
                continue
            } else {
                try {
                    this.fillEntity(entityCreator, args.subList(1, args.size))
                } catch (e: Throwable) {
                    entityCreator.cancel()
                    throw e
                }
                this.commandsContext.executeCommand(this.console, command, entityCreator)
            }
        }
    }

    private fun fillEntity(entity: EntityCreator, args: List<String>) {

    }
}