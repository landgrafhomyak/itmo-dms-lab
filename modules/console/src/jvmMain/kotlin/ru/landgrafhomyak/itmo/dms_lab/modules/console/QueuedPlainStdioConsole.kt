package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import kotlin.io.print as kotlinPrint
import kotlin.io.println as kotlinPrintln
import kotlin.io.readlnOrNull as kotlinReadlnOrNull

class QueuedPlainStdioConsole(maxPendingRequests: UInt = DEFAULT_MAX_REQUESTS) : QueuedConsole(maxPendingRequests) {
    override fun blockingReadln(): String? =
        kotlinReadlnOrNull()

    override fun blockingPrint(s: String) =
        kotlinPrint(s)

    override fun blockingPrintln(s: String) =
        kotlinPrintln(s)

    override fun blockingSetStyle(s: ConsoleTextStyle) {}
}