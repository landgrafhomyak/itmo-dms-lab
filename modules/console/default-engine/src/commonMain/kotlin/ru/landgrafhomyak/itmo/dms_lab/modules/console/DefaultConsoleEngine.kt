package ru.landgrafhomyak.itmo.dms_lab.modules.console.engine

import ru.landgrafhomyak.itmo.dms_lab.modules.action.ActionIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.console.PrefixedConsoleWrapper
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator
import ru.landgrafhomyak.itmo.dms_lab.modules.action.Environment
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer
import ru.landgrafhomyak.itmo.dms_lab.modules.console.ArgsParser
import ru.landgrafhomyak.itmo.dms_lab.modules.console.StopConsoleInteraction


class DefaultConsoleEngine(
    private val console: ConsoleInterface,
    private val environment: Environment,
    private val storage: StorageClientLayer,
) {
    private val spaceRegexp = Regex("""\s+""")
    private val innerConsole = PrefixedConsoleWrapper("|", this.console)
    suspend fun mainloop() {
        while (true) {
            this.console.setStyle(ConsoleTextStyle.UTILITY)
            this.console.print("> ")
            this.console.setStyle(ConsoleTextStyle.INPUT)
            val rawCommand = this.console.readln()
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
            if (rawCommand == null) {
                this.console.setStyle(ConsoleTextStyle.UTILITY)
                this.console.println("\nInput stream ended, exiting")
                this.console.setStyle(ConsoleTextStyle.DEFAULT)
                return
            }
            val (extractedRawCommand, firstLine) = rawCommand
                .trimStart()
                .split(this.spaceRegexp, 2)
                .let { l -> l.first() to l.getOrNull(1) }
            val command = this.environment.actionsSet.dispatchCommand(extractedRawCommand)
            if (command == null) {
                this.console.setStyle(ConsoleTextStyle.ERROR)
                this.console.println("Unknown action")
                this.console.setStyle(ConsoleTextStyle.DEFAULT)
                continue
            }
            this.environment.addActionToHistory(command)
            val io = this.ActionIoProviderImpl(firstLine)

            try {
                command.executeIO(this.storage, io, this.environment)
            } catch (_: StopConsoleInteraction) {
                this.console.setStyle(ConsoleTextStyle.UTILITY)
                this.console.println("Console stopped by action")
                this.console.setStyle(ConsoleTextStyle.DEFAULT)
                break
            } catch (t: Throwable) {
                this.console.setStyle(ConsoleTextStyle.UTILITY)
                this.console.println("Uncaught error wile running action")
                this.console.setStyle(ConsoleTextStyle.ERROR)
                this.console.println(t.stackTraceToString())
            } finally {
                io.isWritable = false
                io.isReadable = false
            }
        }
    }

    private inner class ActionIoProviderImpl(
        private var firstLine: String?
    ) : ActionIoProvider {
        var isReadable = true
        var isWritable = true

        @Suppress("FunctionName")
        private fun _assertIsWritable() {
            if (!this.isWritable) throw IllegalStateException()
        }

        @Suppress("FunctionName")
        private fun _assertIsReadable() {
            if (!this.isReadable) throw IllegalStateException()
        }

        override suspend fun finishArgsReading(): Boolean {
            this._assertIsReadable()
            this.isReadable = false
            if (this.firstLine != null) {
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                this@DefaultConsoleEngine.innerConsole.println("This action doesn't has arguments")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                return true
            }
            return false
        }

        override suspend fun readln(): String? {
            this._assertIsReadable()
            val fL = this.firstLine
            if (fL != null) {
                this.firstLine = null
                return this.firstLine
            }
            return this@DefaultConsoleEngine.innerConsole.readln()
        }

        override suspend fun print(s: String) {
            this._assertIsWritable()
            this@DefaultConsoleEngine.innerConsole.print(s)

        }

        override suspend fun println(s: String) {
            this._assertIsWritable()
            this@DefaultConsoleEngine.innerConsole.println(s)
        }

        override suspend fun setStyle(style: ConsoleTextStyle) {
            this._assertIsWritable()
            this@DefaultConsoleEngine.innerConsole.setStyle(style)
        }

        override suspend fun fillEntity(mutator: EntityMutator): Boolean {
            this._assertIsReadable()
            this.isReadable = false
            if (this.firstLine != null) {
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                this@DefaultConsoleEngine.innerConsole.println("This action doesn't has simple arguments")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
            }
            val rawArgs = this.readln()
            if (rawArgs == null) {
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                this@DefaultConsoleEngine.innerConsole.println("\nInput stream ended but not all attributes are read, entity creating aborted, exiting")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                return false
            }
            return this._fillEntity(mutator, ArgsParser.parseToList(rawArgs))
        }

        @Suppress("FunctionName")
        private suspend fun _fillEntity(entity: EntityMutator, args: List<String>, depth: UInt = 1u): Boolean {
            val primitive = ArrayList<EntityAttributeDescriptor<*, *>>()
            val complex = ArrayList<EntityAttributeDescriptor.ComplexAttribute>()
            for (attr in entity.descriptor) {
                if (attr is EntityAttributeDescriptor.ComplexAttribute)
                    complex.add(attr)
                else
                    primitive.add(attr)
            }
            if (args.size < primitive.size) {
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                this@DefaultConsoleEngine.innerConsole.println("Some primitive attributes not set: ${primitive.asSequence().drop(args.size).joinToString { a -> a.name }}")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.TIP)
                this@DefaultConsoleEngine.innerConsole.println("To pass optional attribute use two double-quotes \"\"")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
            } else if (args.size > primitive.size) {
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                this@DefaultConsoleEngine.innerConsole.println("There ${args.size - primitive.size} extra primitive attributes")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
            }

            for ((d, a) in (primitive zip args)) {
                when (d) {
                    is EntityAttributeDescriptor.IntAttribute -> {
                        if (a.isEmpty() && d is EntityAttributeDescriptor.IntAttribute.Optional) {
                            entity[d] = null
                        } else {
                            val v = this@DefaultConsoleEngine.catchIAE(d.name) { a.toLong() } ?: return false
                            if (!d.checkValid(v)) {
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                                this@DefaultConsoleEngine.innerConsole.println("Attribute '${d.name}' has wrong value")
                                if (d is EntityAttributeDescriptor.IntAttribute.Optional) {
                                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.TIP)
                                    this@DefaultConsoleEngine.innerConsole.println("To pass optional argument write \"\"")
                                }
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                                return false
                            }
                            entity[d] = v
                        }
                    }

                    is EntityAttributeDescriptor.FloatAttribute -> {
                        if (a.isEmpty() && d is EntityAttributeDescriptor.FloatAttribute.Optional) {
                            entity[d] = null
                        } else {
                            val v = this@DefaultConsoleEngine.catchIAE(d.name) { a.toDouble() } ?: return false

                            if (!d.checkValid(v)) {
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                                this@DefaultConsoleEngine.innerConsole.println("Attribute '${d.name}' has wrong value")
                                if (d is EntityAttributeDescriptor.FloatAttribute.Optional) {
                                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.TIP)
                                    this@DefaultConsoleEngine.innerConsole.println("To pass optional argument write \"\"")
                                }
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
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
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                                this@DefaultConsoleEngine.innerConsole.println("Attribute '${d.name}' has wrong value")
                                if (d is EntityAttributeDescriptor.StringAttribute.Optional) {
                                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.TIP)
                                    this@DefaultConsoleEngine.innerConsole.println("To pass optional argument write \"\"")
                                }
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
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
                            this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                            this@DefaultConsoleEngine.innerConsole.println("Attribute '${d.name}' has wrong value")
                            this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.TIP)
                            this@DefaultConsoleEngine.innerConsole.println("Boolean value can be passed as + or - signs, 'true' or 'false' keywords and \"\" (empty argument) if optional")
                            this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                        }
                    }

                    is EntityAttributeDescriptor.EnumAttribute<*> -> {
                        if (a.isEmpty() && d is EntityAttributeDescriptor.EnumAttribute.Optional<*>) {
                            entity[d] = null
                        } else {
                            val v = d.valueFromString(a)
                            if (v == null) {
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                                this@DefaultConsoleEngine.innerConsole.println("Attribute '${d.name}' has wrong value")
                                if (d is EntityAttributeDescriptor.EnumAttribute.Optional<*>) {
                                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.TIP)
                                    this@DefaultConsoleEngine.innerConsole.println("To pass optional argument write \"\"")
                                }
                                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                            }
                        }
                    }

                    else -> throw RuntimeException("Unknown primitive type descriptor: ${d::class.qualifiedName}: $d")
                }
            }

            for (d in complex) {
                val recEntity = entity[d]
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.UTILITY)
                this@DefaultConsoleEngine.innerConsole.print("${">".repeat(depth.toInt())} {${d.targetEntity.name}} ")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.HIGHLIGHT)
                this@DefaultConsoleEngine.innerConsole.print(d.name)
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.UTILITY)
                this@DefaultConsoleEngine.innerConsole.print(" > ")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.INPUT)
                val rawCommand = this@DefaultConsoleEngine.innerConsole.readln()
                if (rawCommand == null) {
                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                    this@DefaultConsoleEngine.innerConsole.println("\nInput stream ended but not all attributes are read, entity creating aborted, exiting")
                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                    return false
                }
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                val recArgs = ArgsParser.parseToList(rawCommand)
                if (!this._fillEntity(recEntity, recArgs, depth + 1u))
                    return false
            }
            return true
        }
    }

    @Suppress("LiftReturnOrAssignment")
    private suspend inline fun <T : Any> catchIAE(attrName: String, block: () -> T): T? {
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