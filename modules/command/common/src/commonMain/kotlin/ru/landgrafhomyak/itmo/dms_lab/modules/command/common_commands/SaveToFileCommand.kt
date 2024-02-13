package ru.landgrafhomyak.itmo.dms_lab.modules.command.common_commands

import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer

class SaveToFileCommand(private val filename: String? = null) : ConsoleCommand {
    override val name: String
        get() = "save"
    override val description: String
        get() = "Saves all changes to file (from which it was loaded)"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        if (io.assertNoArgs()) return
        io.setStyle(ConsoleTextStyle.DEFAULT)
        if (this.filename == null)
            io.println("Saving storage...")
        else
            io.println("Saving storage to '${this.filename}'...")
        storage.commit()
        io.println("Storage saved successfully!")
    }
}