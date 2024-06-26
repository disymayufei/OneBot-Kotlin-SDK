package cn.disy920.okapi.packet.api.group

import cn.disy920.okapi.network.connection.BotConnection.Companion.pack
import cn.disy920.okapi.packet.OneBotPacket

class KickGroupMemberPacket(
    val groupId: Long,
    val userId: Long,
    val rejectAddRequest: Boolean = false
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return pack(
            "set_group_kick",
            mapOf(
                "group_id" to groupId,
                "user_id" to userId,
                "reject_add_request" to rejectAddRequest
            ),
            echo
        ).toString()
    }
}