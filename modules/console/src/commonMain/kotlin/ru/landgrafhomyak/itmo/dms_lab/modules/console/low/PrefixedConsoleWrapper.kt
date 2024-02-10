package ru.landgrafhomyak.itmo.dms_lab.modules.console.low

class PrefixedConsoleWrapper(
    private val prefix: String,
    private val origin: ConsoleInterface
) : ConsoleInterface by origin {
    private var isNewLine = true
    private val prefixLn = "\n" + this.prefix

    override suspend fun print(s: String) {
        @Suppress("RemoveRedundantQualifierName")
        val ss = PrefixedConsoleWrapper.newLinePattern.replace(s, this.prefixLn)
        if (this.isNewLine) {
            this.origin.print(this.prefix + ss)
            this.isNewLine = false
        } else {
            this.origin.print(ss)
        }
    }

    override suspend fun println(s: String) {
        @Suppress("RemoveRedundantQualifierName")
        val ss = PrefixedConsoleWrapper.newLinePattern.replace(s, this.prefixLn)
        if (this.isNewLine) {
            this.origin.println(this.prefix + ss)
            this.isNewLine = true
        } else {
            this.origin.println(ss)
        }
    }

    companion object {
        private val newLinePattern = Regex("\n")
    }
}