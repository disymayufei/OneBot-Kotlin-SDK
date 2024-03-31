package cn.disy920.oks.packet.api.group

import cn.disy920.oks.network.connection.BotConnection
import cn.disy920.oks.packet.OneBotPacket

/**
 * 获取群成员列表的包
 * @param groupId 群号
 */
class RequireGroupMemberListPacket(
    val groupId: Long
) : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        return BotConnection.pack(
            "get_group_member_list",
            mapOf(
                "group_id" to groupId
            ),
            echo
        ).toString()
    }
}