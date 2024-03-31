package cn.disy920.oks.event.notice.admin

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Group
import cn.disy920.oks.contact.GroupMember

/**
 * 群管理员增加事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 管理变动的成员
 * @see AdminChangeEvent
 */
class AdminIncreaseEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
) : AdminChangeEvent(time, bot, group, member)