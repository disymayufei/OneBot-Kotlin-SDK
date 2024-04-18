package cn.disy920.okapi.packet.api.group

import cn.disy920.okapi.network.connection.BotConnection.Companion.pack
import cn.disy920.okapi.packet.OneBotPacket

/**
 * 获取群成员信息的包
 * @param groupId 群号
 * @param userId 用户QQ号
 * @param useCache 是否使用缓存
 * @see OneBotPacket
 */
class RequireGroupMemberInfoPacket(
    val groupId: Long,
    val userId: Long,
    val useCache: Boolean = false
) : OneBotPacket {

    override fun toJsonString(echo: String?): String {
        return pack(
            "get_group_member_info",
            mapOf(
                "group_id" to groupId,
                "user_id" to userId,
                "no_cache" to !useCache
            ),
            echo
        ).toString()
    }
}