package cn.disy920.okapi.event.request

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.contact.Group
import cn.disy920.okapi.contact.Stranger
import cn.disy920.okapi.event.GroupEvent

/**
 * 群成员加群请求事件
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param group 事件发生的群
 * @param member 发送请求的成员
 * @param comment 加群验证消息
 * @param type 请求类型
 * @see RequestEvent
 */
class GroupRequestEvent(
    override val time: Long,
    override val bot: Bot,
    override val flag: String,
    override val group: Group,
    val stranger: Stranger,
    val comment: String,
    val type: Type
) : RequestEvent(time, bot, flag), GroupEvent {

    enum class Type {
        ADD, INVITE
    }
}