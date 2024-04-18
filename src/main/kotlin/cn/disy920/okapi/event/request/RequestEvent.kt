package cn.disy920.okapi.event.request

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.event.AbstractEvent

/**
 * 代表OneBot规范下的请求事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param flag 请求标识
 * @see AbstractEvent
 */
abstract class RequestEvent(
    override val time: Long,
    override val bot: Bot,
    open val flag: String
) : AbstractEvent(time, bot, EventType.REQUEST)