package ru.landgrafhomyak.itmo.dms_lab.modules.console.engine

import ru.landgrafhomyak.itmo.dms_lab.modules.console.low.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.low.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.low.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor

class StupidConsoleEngine(
    console: ConsoleInterface,
    private val commandsContext: CommandsContext,
    isInner: Boolean = false
) {
    private val console = if (!isInner) console else PrefixedConsoleWrapper("|", console)

    suspend fun mainloop() {
        while (true) {
            this.console.setStyle(ConsoleTextStyle.UTILITY)
            this.console.print("> ")
            this.console.setStyle(ConsoleTextStyle.INPUT)
            val rawCommand = this.console.readln()
            if (rawCommand == null) {
                this.console.setStyle(ConsoleTextStyle.UTILITY)
                this.console.println("\nInput stream ended, exiting")
                this.console.setStyle(ConsoleTextStyle.DEFAULT)
                return
            }
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
            val args = ArgsParser.parseToList(rawCommand)
            if (args.isEmpty())
                continue
            val command = args[0]
            val entityCreator = this.commandsContext.isEntityCommand(command)
            if (entityCreator == null) {
                this.commandsContext.executeCommand(this.console, command, args.subList(1, args.size))
                continue
            } else {
                val isCreated = this.fillEntity(entityCreator, args.subList(1, args.size))
                if (isCreated)
                    this.commandsContext.executeCommand(this.console, command, entityCreator)
                continue
            }
        }
    }

    private fun fillEntity(entity: CommandsContext.EntityCreatorForCommand, args: List<String>, depth: UInt = 1u): Boolean {
        val primitive = ArrayList<EntityAttributeDescriptor<*, *>>()
        val complex = ArrayList<EntityAttributeDescriptor.ComplexAttribute>()
        for (attr in entity.descriptor) {
            if (attr is EntityAttributeDescriptor.ComplexAttribute)
                complex.add(attr)
            else
                primitive.add(attr)
        }
        if (args.size < primitive.size) {
            this.console.setStyle(ConsoleTextStyle.ERROR)
            this.console.println("Some primitive attributes not set: ${primitive.asSequence().drop(args.size).joinToString { a -> a.name }}")
            this.console.setStyle(ConsoleTextStyle.TIP)
            this.console.println("To pass optional attribute use two double-quotes \"\"")
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
        } else if (args.size > primitive.size) {
            this.console.setStyle(ConsoleTextStyle.ERROR)
            this.console.println("There ${args.size - primitive.size} extra primitive attributes")
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
        }

        for ((d, a) in (primitive zip args)) {
            when (d) {
                is EntityAttributeDescriptor.IntAttribute -> {
                    if (a.isEmpty() && d is EntityAttributeDescriptor.IntAttribute.Optional) {
                        entity[d] = null
                    } else {
                        val v = this.catchIAE(d.name) { a.toLong() } ?: return false
                        if (!d.checkValid(v)) {
                            this.console.setStyle(ConsoleTextStyle.ERROR)
                            this.console.println("Attribute '${d.name}' has wrong value")
                            if (d is EntityAttributeDescriptor.IntAttribute.Optional) {
                                this.console.setStyle(ConsoleTextStyle.TIP)
                                this.console.println("To pass optional argument write \"\"")
                            }
                            this.console.setStyle(ConsoleTextStyle.DEFAULT)
                            return false
                        }
                        entity[d] = v
                    }
                }

                is EntityAttributeDescriptor.FloatAttribute -> {
                    if (a.isEmpty() && d is EntityAttributeDescriptor.FloatAttribute.Optional) {
                        entity[d] = null
                    } else {
                        val v = this.catchIAE(d.name) { a.toDouble() } ?: return false

                        if (!d.checkValid(v)) {
                            this.console.setStyle(ConsoleTextStyle.ERROR)
                            this.console.println("Attribute '${d.name}' has wrong value")
                            if (d is EntityAttributeDescriptor.FloatAttribute.Optional) {
                                this.console.setStyle(ConsoleTextStyle.TIP)
                                this.console.println("To pass optional argument write \"\"")
                            }
                            this.console.setStyle(ConsoleTextStyle.DEFAULT)
                            return false
                        }
                        entity[d] = v
                    }
                }

                is EntityAttributeDescriptor.StringAttribute -> {
                    if (a.isEmpty() && d is EntityAttributeDescriptor.StringAttribute.Optional) {
                        entity[d] = null
                    } else {
                        if (!d.checkValid(a)) {
                            this.console.setStyle(ConsoleTextStyle.ERROR)
                            this.console.println("Attribute '${d.name}' has wrong value")
                            if (d is EntityAttributeDescriptor.StringAttribute.Optional) {
                                this.console.setStyle(ConsoleTextStyle.TIP)
                                this.console.println("To pass optional argument write \"\"")
                            }
                            this.console.setStyle(ConsoleTextStyle.DEFAULT)
                            return false
                        }
                        entity[d] = a
                    }
                }

                is EntityAttributeDescriptor.BooleanAttribute -> @Suppress("DEPRECATION") when (a.toLowerCase()) {
                    "" -> if (d is EntityAttributeDescriptor.BooleanAttribute.Optional)
                        entity[d] = null

                    "+", "true" -> entity[d] = true
                    "-", "false" -> entity[d] = false
                    "else" -> {
                        this.console.setStyle(ConsoleTextStyle.ERROR)
                        this.console.println("Attribute '${d.name}' has wrong value")
                        this.console.setStyle(ConsoleTextStyle.TIP)
                        this.console.println("Boolean value can be passed as + or - signs, 'true' or 'false' keywords and \"\" (empty argument) if optional")
                        this.console.setStyle(ConsoleTextStyle.DEFAULT)
                    }
                }

                is EntityAttributeDescriptor.EnumAttribute<*> -> {
                    if (a.isEmpty() && d is EntityAttributeDescriptor.EnumAttribute.Optional<*>) {
                        entity[d] = null
                    } else {
                        val v = d.valueFromString(a)
                        if (v == null) {
                            this.console.setStyle(ConsoleTextStyle.ERROR)
                            this.console.println("Attribute '${d.name}' has wrong value")
                            if (d is EntityAttributeDescriptor.EnumAttribute.Optional<*>) {
                                this.console.setStyle(ConsoleTextStyle.TIP)
                                this.console.println("To pass optional argument write \"\"")
                            }
                            this.console.setStyle(ConsoleTextStyle.DEFAULT)
                        }
                    }
                }

                else -> throw RuntimeException("Unknown primitive type descriptor: ${d::class.qualifiedName}: $d")
            }
        }

        for (d in complex) {
            val recEntity = entity[d]
            this.console.setStyle(ConsoleTextStyle.UTILITY)
            this.console.print("${">".repeat(depth.toInt())} {${d.targetEntity.name}} ")
            this.console.setStyle(ConsoleTextStyle.HIGHLIGHT)
            this.console.print(d.name)
            this.console.setStyle(ConsoleTextStyle.UTILITY)
            this.console.print(" > ")
            this.console.setStyle(ConsoleTextStyle.INPUT)
            val rawCommand = this.console.readln()
            if (rawCommand == null) {
                this.console.setStyle(ConsoleTextStyle.ERROR)
                this.console.println("\nInput stream ended but not all attributes are read, entity creating aborted, exiting")
                this.console.setStyle(ConsoleTextStyle.DEFAULT)
                return false
            }
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
            val recArgs = ArgsParser.parseToList(rawCommand)
            if (!this.fillEntity(recEntity, recArgs, depth + 1u))
                return false
        }
        return true
    }

    @Suppress("LiftReturnOrAssignment")
    private inline fun <T : Any> catchIAE(attrName: String, block: () -> T): T? {
        try {
            return block()
        } catch (e: IllegalArgumentException) {
            this.console.setStyle(ConsoleTextStyle.ERROR)
            this.console.println("Error in parsing attribute '${attrName}': ${e.message}")
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
            return null
        }
    }
}