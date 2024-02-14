package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle

class PrefixedConsoleWrapper(
    private val prefix: String,
    private val origin: ConsoleInterface
) : ConsoleInterface by origin {
    private var isNewLine = true
    private var style = ConsoleTextStyle.DEFAULT

    override suspend fun print(s: String) {
        val ss = s.split("\n").iterator()
        if (!ss.hasNext())
            return
        if (!this.isNewLine) {
            val line = ss.next()
            if (ss.hasNext())
                this.origin.println(line)
            else
                this.origin.print(line)
        }
        for (line in ss) {
            if (ss.hasNext()) {
                this.origin.setStyle(ConsoleTextStyle.UTILITY)
                this.origin.print(this.prefix)
                this.origin.setStyle(this.style)
                this.origin.println(line)
            } else {
                if (line.isNotEmpty()) {
                    this.origin.setStyle(ConsoleTextStyle.UTILITY)
                    this.origin.print(this.prefix)
                    this.origin.setStyle(this.style)
                    this.origin.print(line)
                    this.isNewLine = false
                    break
                } else {
                    this.isNewLine = true
                    break
                }
            }
        }
    }

    override suspend fun println(s: String) {
        this.print(s)
        if (this.isNewLine) {
            this.origin.setStyle(ConsoleTextStyle.UTILITY)
            this.origin.print(this.prefix)
            this.origin.setStyle(this.style)
        }
        this.origin.print("\n")
        this.isNewLine = true
    }

    override suspend fun setStyle(style: ConsoleTextStyle) {
        this.style = style
        this.origin.setStyle(style)
    }
}