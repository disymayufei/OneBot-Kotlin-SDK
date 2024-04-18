package cn.disy920.okapi.event.notice.group

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Group
import cn.disy920.okapi.contact.GroupMember
import cn.disy920.okapi.event.GroupMemberEvent
import cn.disy920.okapi.event.notice.NoticeEvent

/**
 * 群禁言事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 被禁言的成员
 * @param operatorId 操作者QQ号
 * @param duration 禁言时长，单位秒
 */
class GroupMuteEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
    val operatorId: Long,
    val duration: Long
) : NoticeEvent(time, bot), GroupMemberEvent