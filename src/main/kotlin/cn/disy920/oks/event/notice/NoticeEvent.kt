package cn.disy920.oks.event.notice

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.event.AbstractEvent
import cn.disy920.oks.event.Event

/**
 * 代表OneBot规范下的通知事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @see Event
 */
abstract class NoticeEvent(
    override val time: Long,
    override val bot: Bot
) : AbstractEvent(time, bot, EventType.NOTICE)