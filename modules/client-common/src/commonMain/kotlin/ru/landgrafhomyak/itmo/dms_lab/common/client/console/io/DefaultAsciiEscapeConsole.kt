package ru.landgrafhomyak.itmo.dms_lab.common.client.console.io

class DefaultAsciiEscapeConsole(
    private val colors: Map<ConsoleColor, String>,
    private val console: ConsoleInterface = DefaultPlainConsole
) : ConsoleInterface by console {
    private val defaultColor = this.colors[ConsoleColor.DEFAULT] ?: throw IllegalArgumentException("Missed default color")

    init {
        this.print(this.defaultColor)
    }

    override fun setColor(color: ConsoleColor) = this.print(this.colors[color] ?: this.defaultColor)

    override fun setUnderline(underline: Boolean) =
        if (underline) this.print("\u001b[4m")
        else this.print("\u001b[24m")
}