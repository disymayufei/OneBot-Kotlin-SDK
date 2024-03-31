package cn.disy920.oks.event.notice.group

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Group
import cn.disy920.oks.contact.GroupMember
import cn.disy920.oks.event.notice.group.GroupMemberIncreaseEvent.Type.APPROVE
import cn.disy920.oks.event.notice.group.GroupMemberIncreaseEvent.Type.INVITE

/**
 * 群成员增加事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 事件发生的成员
 * @param operatorId 操作者QQ号
 * @param type 群员增加类型
 */
class GroupMemberIncreaseEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
    override val operatorId: Long,
    val type: Type
) : GroupMemberChangeEvent(time, bot, group, member, operatorId) {

    /**
     * 群成员增加类型
     * @property INVITE 被邀请入群
     * @property APPROVE 主动加群
     */
    enum class Type {
        INVITE, APPROVE
    }
}