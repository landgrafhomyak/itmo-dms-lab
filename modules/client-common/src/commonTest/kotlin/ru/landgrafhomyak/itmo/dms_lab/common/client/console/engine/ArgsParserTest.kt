package ru.landgrafhomyak.itmo.dms_lab.common.client.console.engine

import kotlin.test.Test
import kotlin.test.assertContentEquals

internal class ArgsParserTest {
    private fun assert(input: String, vararg parsed: String) {
        val expected = parsed.asList()
        val actual = ArgsParser.parseToList(input)
        assertContentEquals(expected, actual)
    }

    @Test
    fun testEmpty() = assert("")

    @Test
    fun testEmptyWhitespaces() = assert("    \t  \t    \t\n")

    @Test
    fun testOne() = assert("one", "one")

    @Test
    fun testOneWhitespaces() = assert("   \t   \none\t    ", "one")

    @Test
    fun testTwo() = assert("   \t   \none\t   two     \n ", "one", "two")

    @Test
    fun testEscape() = assert("   \t   o\\ne\t   \\two     \n ", "o\ne", "\two")

    @Test
    fun testQuote() = assert("   \t   \"o\\ne\t  \\\"  \\two\"     \n ", "o\ne\t  \"  \two")
}