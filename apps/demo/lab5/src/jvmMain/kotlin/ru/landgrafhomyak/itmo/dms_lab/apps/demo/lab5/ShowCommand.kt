package ru.landgrafhomyak.itmo.dms_lab.apps.demo.lab5

import ru.landgrafhomyak.itmo.dms_lab.models.demo.DemoRootEntityDescriptor
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommand
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandEnvironment
import ru.landgrafhomyak.itmo.dms_lab.modules.command.ConsoleCommandIoProvider
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.StorageClientLayer
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract.readRemainingSafe
import ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.common_filters.SelectAllFilter

object ShowCommand : ConsoleCommand {
    override val name: String
        get() = "show"
    override val description: String
        get() = "Shows all entities in storage ith all changes"

    override suspend fun execute(storage: StorageClientLayer, io: ConsoleCommandIoProvider, environment: ConsoleCommandEnvironment) {
        var anyPrinted = false
        storage.startActionByFilter(SelectAllFilter).select().readRemainingSafe { e ->
            anyPrinted = true
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print("{")
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.print(DemoRootEntityDescriptor.ComparationKeyAttribute.name)
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print(":")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.print(e[DemoRootEntityDescriptor.ComparationKeyAttribute].toString())

            val optStr = e[DemoRootEntityDescriptor.SomeStringAttribute]
            if (optStr != null) {
                io.setStyle(ConsoleTextStyle.UTILITY)
                io.print(" ")
                io.setStyle(ConsoleTextStyle.HIGHLIGHT)
                io.print(DemoRootEntityDescriptor.SomeStringAttribute.name)
                io.setStyle(ConsoleTextStyle.UTILITY)
                io.print(":")
                io.setStyle(ConsoleTextStyle.DEFAULT)
                io.print(optStr)
            }
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print(" ")
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.print(DemoRootEntityDescriptor.CoordinatesDescriptor.name)
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print(":{")
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.print(DemoRootEntityDescriptor.CoordinatesAttribute.XAttribute.name)
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print(":")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.print(e[DemoRootEntityDescriptor.CoordinatesAttribute][DemoRootEntityDescriptor.CoordinatesAttribute.XAttribute].toString())
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print(" ")
            io.setStyle(ConsoleTextStyle.HIGHLIGHT)
            io.print(DemoRootEntityDescriptor.CoordinatesAttribute.YAttribute.name)
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.print(":")
            io.setStyle(ConsoleTextStyle.DEFAULT)
            io.print(e[DemoRootEntityDescriptor.CoordinatesAttribute][DemoRootEntityDescriptor.CoordinatesAttribute.YAttribute].toString())
            io.setStyle(ConsoleTextStyle.UTILITY)
            io.println("} }")
            io.setStyle(ConsoleTextStyle.DEFAULT)
        }
        if (!anyPrinted) {
            io.setStyle(ConsoleTextStyle.TIP)
            io.println("Storage is empty")
            io.setStyle(ConsoleTextStyle.DEFAULT)
        }
    }
}