package cn.disy920.oks.event.meta.lifecycle

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.event.meta.MetaEvent

/**
 * 代表OneBot的生命周期事件，用于描述OneBot所处的运行状态
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @see MetaEvent
 */
abstract class LifeCycleEvent(
    override val time: Long,
    override val bot: Bot
) : MetaEvent(time, bot)