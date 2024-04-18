package cn.disy920.okapi.packet.api.friend

import cn.disy920.okapi.message.chain.MessageChain
import cn.disy920.okapi.network.connection.BotConnection.Companion.pack
import cn.disy920.okapi.packet.OneBotPacket

/**
 * 发送私聊消息的包
 * @param userId 对方的QQ号
 * @param message 消息内容
 * @see OneBotPacket
 */
class SendPrivateMsgPacket(
    val userId: Long,
    val message: MessageChain
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return pack(
            "send_private_msg",
            mapOf(
                "user_id" to userId,
                "message" to message.serializeToJson()
            ),
            echo
        ).toString()
    }
}