package cn.disy920.okapi.packet.api.friend

import cn.disy920.okapi.annotation.Beta
import cn.disy920.okapi.message.chain.MessageChain
import cn.disy920.okapi.network.connection.BotConnection
import cn.disy920.okapi.packet.OneBotPacket

@Beta("官方版LLOneBot暂时不支持发送临时会话消息，仅限@disymayufei的fork版支持")
class SendTempMsgPacket(
    val groupId: Long,
    val userId: Long,
    val message: MessageChain
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return BotConnection.pack(
            "send_msg",
            mapOf(
                "group_id" to groupId,
                "user_id" to userId,
                "message" to message.serializeToJson()
            ),
            echo
        ).toString()
    }
}