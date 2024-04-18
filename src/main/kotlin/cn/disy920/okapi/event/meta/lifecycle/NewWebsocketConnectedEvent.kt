package cn.disy920.okapi.event.meta.lifecycle

import cn.disy920.okapi.contact.Bot

/**
 * 当一个新的Bot Websocket连接成功时会触发本事件，仅限正/反向Websocket可以接收该事件
 * @param time 事件发生的时间
 * @param bot 事件对应的机器人
 * @see LifeCycleEvent
 */
class NewWebsocketConnectedEvent(
    override val time: Long,
    override val bot: Bot
) : LifeCycleEvent(time, bot)