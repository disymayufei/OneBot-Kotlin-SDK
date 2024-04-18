package cn.disy920.okapi.event

import cn.disy920.okapi.contact.GroupMember

/**
 * 群成员事件
 * @see GroupEvent
 */
interface GroupMemberEvent : GroupEvent {
    val member: GroupMember
}