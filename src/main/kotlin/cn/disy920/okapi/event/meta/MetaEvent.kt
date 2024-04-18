package cn.disy920.okapi.event.meta

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.event.AbstractEvent
import cn.disy920.okapi.event.Event

/**
 * 代表OneBot规范下的元事件，通常用于描述OneBot自身的状态情况，而非聊天软件的状态
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @see Event
 */
abstract class MetaEvent(
    override val time: Long,
    override val bot: Bot,
) : AbstractEvent(time, bot, EventType.META)