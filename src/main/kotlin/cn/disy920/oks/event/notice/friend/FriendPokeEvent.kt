package cn.disy920.oks.event.notice.friend

import cn.disy920.oks.annotation.Beta
import cn.disy920.oks.annotation.Undefined
import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Friend
import cn.disy920.oks.event.FriendEvent
import cn.disy920.oks.event.notice.NoticeEvent

/**
 * 好友戳一戳事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param friend 发起戳一戳的好友
 * @param targetId 被戳的好友的QQ号
 * @see NoticeEvent
 */
@Beta("仅限Windows系统的NtQQ端可用")
@Undefined("LLOneBot尚未完全实现")
class FriendPokeEvent(
    override val time: Long,
    override val bot: Bot,
    override val friend: Friend,
    val targetId: Long
) : NoticeEvent(time, bot), FriendEvent {
    val sender = friend  // 发起戳一戳的好友
}