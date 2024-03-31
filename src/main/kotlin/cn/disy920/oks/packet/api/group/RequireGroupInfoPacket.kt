package cn.disy920.oks.packet.api.group

import cn.disy920.oks.network.connection.BotConnection.Companion.pack
import cn.disy920.oks.packet.OneBotPacket

/**
 * 获取群信息的包
 * @param groupId 群号
 * @param useCache 是否使用缓存
 * @see OneBotPacket
 */
class RequireGroupInfoPacket(
    val groupId: Long,
    val useCache: Boolean = false
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return pack(
            "get_group_info",
            mapOf(
                "group_id" to groupId,
                "no_cache" to !useCache
            ),
            echo
        ).toString()
    }
}