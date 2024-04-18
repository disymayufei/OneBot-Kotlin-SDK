package cn.disy920.okapi.event.notice.group

import cn.disy920.okapi.annotation.Beta
import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Group
import cn.disy920.okapi.contact.GroupMember
import cn.disy920.okapi.event.GroupMemberEvent
import cn.disy920.okapi.event.notice.NoticeEvent

/**
 * 群成员戳一戳事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 发起戳一戳的群成员
 * @param targetId 被戳的群成员的QQ号
 * @see NoticeEvent
 */
@Beta("仅限Windows系统的NtQQ端可用")
class GroupPokeEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
    val targetId: Long
) : NoticeEvent(time, bot), GroupMemberEvent {
    val sender = member  // 发起戳一戳的群成员
}