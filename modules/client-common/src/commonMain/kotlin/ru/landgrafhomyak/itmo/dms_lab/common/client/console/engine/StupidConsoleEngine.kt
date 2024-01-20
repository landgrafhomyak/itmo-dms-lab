package ru.landgrafhomyak.itmo.dms_lab.common.client.console.engine

import ru.landgrafhomyak.itmo.dms_lab.common.client.console.io.ConsoleColor
import ru.landgrafhomyak.itmo.dms_lab.common.client.console.io.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.common.common.descriptors.EntityAttributeDescriptor

class StupidConsoleEngine(private val console: ConsoleInterface, private val commandsContext: CommandsContext) {
    @Suppress("SpellCheckingInspection")
    fun mainloop() {
        while (true) {
            this.console.setUnderline(false)
            this.console.setColor(ConsoleColor.MINOR)
            this.console.print("> ")
            this.console.setColor(ConsoleColor.DEFAULT)
            val rawCommand = this.console.readln()
            if (rawCommand == null) {
                this.console.setUnderline(false)
                this.console.setColor(ConsoleColor.MINOR)
                this.console.println("\nInput stream ended, exiting")
                this.console.setColor(ConsoleColor.DEFAULT)
                return
            }
            val args = ArgsParser.parseToList(rawCommand)
            if (args.isEmpty())
                continue
            val command = args[0]
            val entityCreator = this.commandsContext.isEntityCommand(command)
            if (entityCreator == null) {
                this.commandsContext.executeCommand(this.console, command, args.subList(1, args.size))
                continue
            } else {
                val isCreated: Boolean
                try {
                    isCreated = this.fillEntity(entityCreator, args.subList(1, args.size))
                } catch (e: Throwable) {
                    entityCreator.cancel()
                    throw e
                }
                if (isCreated)
                    this.commandsContext.executeCommand(this.console, command, entityCreator)
            }
        }
    }

    private fun fillEntity(entity: EntityCreator, args: List<String>, depth: UInt = 1u): Boolean {
        val primitive = ArrayList<EntityAttributeDescriptor>()
        val complex = ArrayList<EntityAttributeDescriptor.InnerEntity>()
        for (attr in entity.descriptor) {
            if (attr is EntityAttributeDescriptor.InnerEntity)
                complex.add(attr)
            else
                primitive.add(attr)
        }
        if (args.size < primitive.size) {
            this.console.setColor(ConsoleColor.ERROR)
            this.console.println("Some primitive attributes not set: ${primitive.asSequence().drop(args.size).joinToString { a -> a.name }}")
            this.console.setColor(ConsoleColor.TIP)
            this.console.println("To pass optional attribute use two double-quotes \"\"")
            this.console.setColor(ConsoleColor.DEFAULT)
        } else if (args.size > primitive.size) {
            this.console.setColor(ConsoleColor.ERROR)
            this.console.println("There ${args.size - primitive.size} extra primitive attributes")
            this.console.setColor(ConsoleColor.DEFAULT)
        }


}