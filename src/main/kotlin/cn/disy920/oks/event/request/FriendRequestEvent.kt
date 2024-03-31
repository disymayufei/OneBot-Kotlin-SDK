package cn.disy920.oks.event.request

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.contact.Stranger

/**
 * 新好友请求事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param userId 发送请求的用户QQ号
 * @param comment 验证信息
 * @see RequestEvent
 */
class FriendRequestEvent(
    override val time: Long,
    override val bot: Bot,
    override val flag: String,
    val user: Stranger,
    val comment: String?
) : RequestEvent(time, bot, flag)