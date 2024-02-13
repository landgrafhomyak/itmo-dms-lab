package ru.landgrafhomyak.itmo.dms_lab.modules.storage_client_layer.abstract

interface Filter {
    suspend fun build(builder: FilterReceiver)
}