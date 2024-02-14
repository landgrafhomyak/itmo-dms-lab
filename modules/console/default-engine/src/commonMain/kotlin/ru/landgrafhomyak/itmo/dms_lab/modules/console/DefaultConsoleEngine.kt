package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityAttributeDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.entity.EntityMutator
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer


class DefaultConsoleEngine(
    private val console: ConsoleInterface,
    private val environment: ConsoleCommandEnvironment,
    private val storage: StorageClientLayer,
) {
    private val spaceRegexp = Regex("""\s+""")
    private val innerConsole = PrefixedConsoleWrapper("|", this.console)
    suspend fun serveForever() {
        while (true) {
            this.console.setStyle(ConsoleTextStyle.UTILITY)
            this.console.print("> ")
            this.console.setStyle(ConsoleTextStyle.INPUT)
            val rawCommand = this.console.readln()
            this.console.setStyle(ConsoleTextStyle.DEFAULT)
            if (rawCommand == null) {
                this._printEof()
                return
            }
            val (extractedRawCommand, firstLine) = rawCommand
                .trimStart()
                .split(this.spaceRegexp, 2)
                .let { l -> l.first() to l.getOrNull(1) }
            val command = this.environment.commandsSet.dispatchCommand(extractedRawCommand)
            if (command == null) {
                this.console.setStyle(ConsoleTextStyle.ERROR)
                this.console.println("Unknown command")
                this.console.setStyle(ConsoleTextStyle.DEFAULT)
                continue
            }
            this.environment.addCommandToHistory(command)
            val io = this.ConsoleCommandIoProviderImpl(firstLine)

            try {
                command.execute(this.storage, io, this.environment)
            } catch (t: Throwable) {
                this.console.setStyle(ConsoleTextStyle.UTILITY)
                this.console.println("Uncaught error wile running command")
                this.console.setStyle(ConsoleTextStyle.ERROR)
                this.console.println(t.stackTraceToString())
            } finally {
                io.isWritable = false
                io.isReadable = false
                if (io.eofReached) {
                    this._printEof()
                    break
                }
                if (io.consoleStopRequested) {
                    this.console.setStyle(ConsoleTextStyle.UTILITY)
                    this.console.println("Console stopped by action")
                    this.console.setStyle(ConsoleTextStyle.DEFAULT)
                    break
                }
            }
        }
    }

    @Suppress("FunctionName")
    private suspend fun _printEof() {
        this.console.setStyle(ConsoleTextStyle.UTILITY)
        this.console.println("\nInput stream ended, exiting")
        this.console.setStyle(ConsoleTextStyle.DEFAULT)
    }

    private inner class ConsoleCommandIoProviderImpl(
        override var argsOrNull: String?
    ) : ConsoleCommandIoProvider {
        var isReadable = true
        var isWritable = true
        var eofReached = false
            private set

        var consoleStopRequested = false
            private set

        @Suppress("FunctionName")
        private fun _assertIsWritable() {
            if (!this.isWritable) throw IllegalStateException()
        }

        @Suppress("FunctionName")
        private fun _assertIsReadable() {
            if (!this.isReadable) throw IllegalStateException()
        }

        override suspend fun readln(): String? {
            this._assertIsReadable()
            val line = this@DefaultConsoleEngine.innerConsole.readln()
            if (line == null)
                this.eofReached = true
            return line
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

        override suspend fun fillEntity(firstLine: String?, target: EntityMutator): Boolean {
            this._assertIsReadable()
            val rawArgs: String?
            if (firstLine != null) {
                rawArgs = firstLine
            } else {
                rawArgs = this.readln()
                if (rawArgs == null) {
                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                    this@DefaultConsoleEngine.innerConsole.println("This action doesn't has arguments")
                    this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                    return true
                }
            }

            return this._fillEntity(target, ArgsParser.parseToList(rawArgs))
        }

        override suspend fun assertNoArgs(): Boolean {
            if (this.argsOrNull != null) {
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.ERROR)
                this@DefaultConsoleEngine.innerConsole.println("This command doesn't has arguments")
                this@DefaultConsoleEngine.innerConsole.setStyle(ConsoleTextStyle.DEFAULT)
                return true
            }
            return false
        }

        override fun scheduleConsoleStop() {
            this.consoleStopRequested = true
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
                this@DefaultConsoleEngine.innerConsole.print("${">".repeat(depth.toInt())} {${d.targetEntityDescriptor.name}} ")
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