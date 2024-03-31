package cn.disy920.oks.event

import cn.disy920.oks.contact.Bot

/**
 * 一个抽象的事件类，是所有事件的基类，实现了基本的[Event]接口
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param eventType 事件类型
 * @see Event
 */
abstract class AbstractEvent(
    open val time: Long,
    open val bot: Bot,
    val eventType: EventType
) : Event {


    val eventName: String = this.javaClass.simpleName

    /**
     * 代表事件的类型，包括消息事件、通知事件、请求事件和元事件，类型遵照OneBot规范
     * @param typeName 事件类型的名称
     * @see AbstractEvent
     */
    enum class EventType(val typeName: String) {
        MESSAGE("message"),  // 消息事件
        NOTICE("notice"),  // 通知事件
        REQUEST("request"),  // 请求事件
        META("meta_event")  // 元事件
    }
}