package cn.disy920.oks.event

import cn.disy920.oks.contact.GroupMember

/**
 * 群成员事件
 * @see GroupEvent
 */
interface GroupMemberEvent : GroupEvent {
    val member: GroupMember
}