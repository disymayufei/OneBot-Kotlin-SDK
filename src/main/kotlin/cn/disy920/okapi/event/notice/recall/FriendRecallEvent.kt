package cn.disy920.okapi.event.notice.recall

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Friend
import cn.disy920.okapi.event.FriendEvent

/**
 * 好友私聊消息撤回事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param friend 撤回者
 * @param messageId 被撤回的消息ID
 * @see MessageRecallEvent
 */
class FriendRecallEvent(
    override val time: Long,
    override val bot: Bot,
    override val friend: Friend,
    override val messageId: Long
) : MessageRecallEvent(time, bot, messageId), FriendEvent