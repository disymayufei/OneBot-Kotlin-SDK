package cn.disy920.oks.event.notice.recall

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.event.notice.NoticeEvent

/**
 * 消息撤回事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param messageId 撤回的消息ID
 * @see NoticeEvent
 */
abstract class MessageRecallEvent(
    override val time: Long,
    override val bot: Bot,
    open val messageId: Long
) : NoticeEvent(time, bot)