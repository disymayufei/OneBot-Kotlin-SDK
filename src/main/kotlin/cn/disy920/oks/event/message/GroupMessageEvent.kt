package cn.disy920.oks.event.message

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Group
import cn.disy920.oks.contact.GroupMember
import cn.disy920.oks.message.chain.MessageChain

class GroupMessageEvent(
    override val time: Long,
    override val bot: Bot,
    override val message: MessageChain,
    override val user: GroupMember,
    val group: Group
) : MessageEvent(time, bot, message, user)