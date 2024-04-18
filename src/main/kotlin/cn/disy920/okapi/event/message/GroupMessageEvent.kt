package cn.disy920.okapi.event.message

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Group
import cn.disy920.okapi.contact.GroupMember
import cn.disy920.okapi.message.chain.MessageChain

class GroupMessageEvent(
    override val time: Long,
    override val bot: Bot,
    override val message: MessageChain,
    override val user: GroupMember,
    val group: Group
) : MessageEvent(time, bot, message, user)