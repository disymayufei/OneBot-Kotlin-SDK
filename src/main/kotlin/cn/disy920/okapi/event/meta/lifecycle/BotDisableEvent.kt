package cn.disy920.okapi.event.meta.lifecycle

import cn.disy920.okapi.annotation.Undefined
import cn.disy920.okapi.contact.Bot

/**
 * OneBot端关闭时会触发本事件，仅限HTTP POST连接可以正常接收该事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @see LifeCycleEvent
 */
@Undefined("该方法尚未被LLOneBot实现")
class BotDisableEvent(
    override val time: Long,
    override val bot: Bot
) : LifeCycleEvent(time, bot)