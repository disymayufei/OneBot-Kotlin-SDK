package cn.disy920.okapi.event

import cn.disy920.okapi.contact.Group

/**
 * 群事件
 * @see Event
 */
interface GroupEvent : Event {
    val group: Group
}