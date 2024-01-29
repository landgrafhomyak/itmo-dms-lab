package ru.landgrafhomyak.itmo.dms_lab.utility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IPv4ParserTest {
    @Test
    fun testValid() = assertEquals(
        IPv4(8u, 8u, 8u, 8u),
        IPv4.parseFromString("8.8.8.8")
    )

    @Test
    fun testValidWithPort() = assertEquals(
        IPv4(127u, 0u, 0u, 1u, 80u),
        IPv4.parseFromString("127.0.0.1:80")
    )

    @Test
    fun testWrongPattern() {
        assertFailsWith(IllegalArgumentException::class) { IPv4.parseFromString("1.1$22") }
    }

    @Test
    fun testOverflow() {
        assertFailsWith(IllegalArgumentException::class) { IPv4.parseFromString("1.1.13241234.1:3423432423") }
    }
}