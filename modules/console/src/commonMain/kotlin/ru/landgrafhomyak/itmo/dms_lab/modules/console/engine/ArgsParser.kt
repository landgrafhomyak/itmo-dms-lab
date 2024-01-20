package ru.landgrafhomyak.itmo.dms_lab.modules.console.engine

internal class ArgsParser(private val data: String) : Iterator<String> {
    private var pos = 0
    private var next: String? = null
    private fun parseNext() {
        if (this.next != null) return
        if (this.pos >= this.data.length) return
        val sb = StringBuilder()
        var insideQuote = false
        var argExists = false

        loop@ while (this.pos < this.data.length && this.data[this.pos].isWhitespace())
            this.pos++

        loop@ while (this.pos < this.data.length) {
            val c = this.data[this.pos]
            when {
                c == '"' -> insideQuote = !insideQuote
                c.isWhitespace() ->
                    if (insideQuote) sb.append(c)
                    else break@loop

                c == '\\' -> {
                    this.pos++
                    if (this.pos >= this.data.length) break@loop
                    when (val o = this.data[pos]) {
                        'n' -> sb.append('\n')
                        't' -> sb.append('\t')
                        '0' -> sb.append('\u0000')
                        else -> sb.append(o)
                    }
                }

                else -> sb.append(c)
            }
            argExists = true
            this.pos++
        }
        if (!argExists) return
        this.next = sb.toString()
    }

    override fun hasNext(): Boolean {
        this.parseNext()
        return this.next != null
    }

    override fun next(): String {
        this.parseNext()
        val next = this.next ?: throw NoSuchElementException()
        this.next = null
        return next
    }

    fun remainingToList(): MutableList<String> {
        val list = ArrayList<String>((this.data.length - this.pos) / 2)
        while (this.hasNext()) {
            list.add(this.next())
        }
        list.trimToSize()
        return list
    }

    companion object {
        @JvmStatic
        fun parseToList(raw: String) = ArgsParser(raw).remainingToList()
    }
}