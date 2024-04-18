package cn.disy920.okapi.contact

import cn.disy920.okapi.annotation.Beta
import cn.disy920.okapi.message.chain.MessageChain
import cn.disy920.okapi.packet.api.friend.SendTempMsgPacket

/**
 * 代表一个陌生人，通常用于创建临时会话
 * @param id 用户的QQ号
 * @param nickName 用户的昵称
 * @param bot 用户所对应的机器人
 */
class Stranger(
    override val id: Long,
    override val nickName: String,
    override val bot: Bot
) : User(id, nickName, bot) {
    companion object {
        fun getEmptyStranger(bot: Bot, id: Long) = Stranger(id, "", bot)
    }

    /**
     * 向特定陌生人发送临时会话消息
     * @param messages 待发送的消息链
     * @param fromGroup 陌生人来自的群
     */
    @Beta("LLOneBot暂时不支持发送临时会话消息，仅限@disymayufei的fork版支持")
    fun sendMessage(messages: MessageChain, fromGroup: Group) {
        bot.getConnection().postMsg(SendTempMsgPacket(fromGroup.id, id, messages))
    }

    /**
     * 向特定陌生人发送临时会话消息
     * @param messages 待发送的消息链
     */
    override fun sendMessage(messages: MessageChain) {
        throw UnsupportedOperationException("Stranger can't send message without a group number.")
    }
}