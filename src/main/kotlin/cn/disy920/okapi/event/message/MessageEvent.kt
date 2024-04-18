package cn.disy920.okapi.event.message

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.User
import cn.disy920.okapi.event.AbstractEvent
import cn.disy920.okapi.message.chain.MessageChain

/**
 * 代表OneBot规范下的消息事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param message 事件对应的消息链
 * @param user 事件对应的用户
 * @see AbstractEvent
 */
abstract class MessageEvent(
    override val time: Long,
    override val bot: Bot,
    open val message: MessageChain,
    open val user: User
) : AbstractEvent(time, bot, EventType.MESSAGE)