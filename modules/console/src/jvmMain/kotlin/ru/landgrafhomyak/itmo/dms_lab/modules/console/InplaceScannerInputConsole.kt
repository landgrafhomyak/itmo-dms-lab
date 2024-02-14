package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.InputConsoleInterface
import java.io.FileReader
import java.io.Reader
import java.nio.file.Path
import java.util.Scanner

class InplaceScannerInputConsole(private val scanner: Scanner) : InputConsoleInterface {
    constructor(path: Path) : this(Scanner(FileReader(path.toFile())))

    override suspend fun readln(): String? {
        if (!this.scanner.hasNextLine())
            return null
        return this.scanner.nextLine()
    }

}