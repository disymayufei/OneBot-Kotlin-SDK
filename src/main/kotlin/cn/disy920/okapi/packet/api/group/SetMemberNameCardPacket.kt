package cn.disy920.okapi.packet.api.group

import cn.disy920.okapi.network.connection.BotConnection.Companion.pack
import cn.disy920.okapi.packet.OneBotPacket

class SetMemberNameCardPacket(
    val groupId: Long,
    val userId: Long,
    val nameCard: String
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return pack(
            "set_group_card",
            mapOf(
                "group_id" to groupId,
                "user_id" to userId,
                "card" to nameCard
            ),
            echo
        ).toString()
    }
}