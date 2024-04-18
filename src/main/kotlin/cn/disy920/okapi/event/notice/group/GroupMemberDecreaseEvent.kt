package cn.disy920.okapi.event.notice.group

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Group
import cn.disy920.okapi.contact.GroupMember
import cn.disy920.okapi.event.notice.group.GroupMemberDecreaseEvent.Type.*

/**
 * 群成员减少事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 事件发生的成员
 * @param operatorId 操作者QQ号
 * @param type 群员减少类型
 */
class GroupMemberDecreaseEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
    override val operatorId: Long,
    val type: Type
) : GroupMemberChangeEvent(time, bot, group, member, operatorId) {
    /**
     * 群员减少类型
     * @property LEAVE 主动退群
     * @property KICK 被踢出群
     * @property KICK_ME 机器人被踢出群
     */
    enum class Type {
        LEAVE, KICK, KICK_ME
    }
}