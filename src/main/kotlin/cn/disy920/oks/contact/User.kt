package cn.disy920.oks.contact

import cn.disy920.oks.message.Message
import cn.disy920.oks.message.PlainMessage
import cn.disy920.oks.message.chain.MessageChain
import cn.disy920.oks.packet.api.friend.SendPrivateMsgPacket

/**
 * 代表一个个人QQ用户
 * @param id 用户的QQ号
 * @param nickName 用户的昵称
 * @param bot 用户所对应的机器人
 * @see UserOrBot
 */
open class User(
    override val id: Long,
    open val nickName: String,
    open val bot: Bot
) : UserOrBot() {

    /**
     * 向用户发送私聊消息
     * @param message 待发送的消息
     */
    open fun sendMessage(message: String) {
        sendMessage(PlainMessage(message))
    }

    /**
     * 向用户发送私聊消息
     * @param message 待发送的消息
     */
    open fun sendMessage(message: Message) {
        sendMessage(MessageChain(MessageChain.NEW_MSG_ID, listOf(message)))
    }

    /**
     * 向用户发送私聊消息
     * @param messages 待发送的消息链
     */
    open fun sendMessage(messages: MessageChain) {
        bot.getConnection().postMsg(SendPrivateMsgPacket(id, messages))
    }
}