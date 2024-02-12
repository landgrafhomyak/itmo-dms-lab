package ru.landgrafhomyak.itmo.dms_lab.modules.action.common_actions

import ru.landgrafhomyak.itmo.dms_lab.modules.action.Action

object RemoveHeadAction : Action by RemoveFirstAction {
    override val name: String
        get() = "remove_head"
}