package ru.landgrafhomyak.itmo.dms_lab.modules.command

class ConsoleCommandsSetMapImpl(vararg commands: ConsoleCommand) : ConsoleCommandsSet {
    private val map = commands.associateBy { c -> c.name }
    override fun dispatchCommand(name: String): ConsoleCommand? =
        this.map[name]

    override fun iterator(): Iterator<ConsoleCommand> =
        this.map.values.iterator()
}