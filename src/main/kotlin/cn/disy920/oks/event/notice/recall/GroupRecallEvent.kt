package cn.disy920.oks.event.notice.recall

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Group
import cn.disy920.oks.contact.GroupMember
import cn.disy920.oks.event.GroupMemberEvent

/**
 * 群消息撤回事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param userId 被撤回者的QQ号
 * @param messageId 被撤回的消息ID
 * @param operatorId 操作者的QQ号
 * @see MessageRecallEvent
 */
class GroupRecallEvent(
    override val time: Long,
    override val bot: Bot,
    override val group: Group,
    override val member: GroupMember,
    override val messageId: Long,
    val operatorId: Long
) : MessageRecallEvent(time, bot, messageId), GroupMemberEvent