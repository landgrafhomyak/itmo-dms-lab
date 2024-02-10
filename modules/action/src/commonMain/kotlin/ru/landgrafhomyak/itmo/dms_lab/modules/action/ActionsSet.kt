package ru.landgrafhomyak.itmo.dms_lab.modules.action

interface ActionsSet : Iterable<Action> {
    fun dispatchCommand(name: String): Action?
}