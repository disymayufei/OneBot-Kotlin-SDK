package cn.disy920.oks.packet.api.friend

import cn.disy920.oks.network.connection.BotConnection
import cn.disy920.oks.packet.OneBotPacket

class RequireStrangerInfoPacket(
    val userId: Long,
    val useCache: Boolean = false
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return BotConnection.pack(
            "get_stranger_info",
            mapOf(
                "user_id" to userId,
                "no_cache" to !useCache
            ),
            echo
        ).toString()
    }
}