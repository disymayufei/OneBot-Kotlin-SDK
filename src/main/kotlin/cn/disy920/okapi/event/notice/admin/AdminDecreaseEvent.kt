package cn.disy920.okapi.event.notice.admin

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Group
import cn.disy920.okapi.contact.GroupMember

/**
 * 群管理员减少事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 管理变动的成员
 * @see AdminChangeEvent
 */
class AdminDecreaseEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
) : AdminChangeEvent(time, bot, group, member)