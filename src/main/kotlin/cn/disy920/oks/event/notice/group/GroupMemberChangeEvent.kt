package cn.disy920.oks.event.notice.group

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Group
import cn.disy920.oks.contact.GroupMember
import cn.disy920.oks.event.GroupMemberEvent
import cn.disy920.oks.event.notice.NoticeEvent

/**
 * 群成员变动事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 事件发生的成员
 * @param operatorId 操作者的QQ号
 * @see NoticeEvent
 */
abstract class GroupMemberChangeEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
    open val operatorId: Long
) : NoticeEvent(time, bot), GroupMemberEvent