package cn.disy920.oks.packet.api.bot

import cn.disy920.oks.packet.OneBotPacket

class RequireLoginInfoPacket : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        echo?.let {
            return "{\"action\":\"get_login_info\",\"echo\":\"$it\"}"
        } ?: let {
            return "{\"action\":\"get_login_info\"}"
        }
    }
}