package cn.disy920.oks.event

import cn.disy920.oks.contact.Group

/**
 * 群事件
 * @see Event
 */
interface GroupEvent : Event {
    val group: Group
}