package cn.disy920.oks.event.message

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Friend
import cn.disy920.oks.message.chain.MessageChain

/**
 * 私聊消息事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param message 事件对应的消息链
 * @param user 事件对应的好友
 */
class PrivateMessageEvent(
    override val time: Long,
    override val bot: Bot,
    override val message: MessageChain,
    override val user: Friend
) : MessageEvent(time, bot, message, user)