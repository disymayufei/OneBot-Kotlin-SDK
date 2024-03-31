package cn.disy920.oks.packet.api.group

import cn.disy920.oks.message.chain.MessageChain
import cn.disy920.oks.network.connection.BotConnection
import cn.disy920.oks.packet.OneBotPacket

class SendGroupMsgPacket(
    val groupId: Long,
    val message: MessageChain
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return BotConnection.pack(
            "send_group_msg",
            mapOf(
                "group_id" to groupId,
                "message" to message.serializeToJson()
            ),
            echo
        ).toString()
    }
}