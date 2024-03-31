package cn.disy920.oks.packet.api.bot

import cn.disy920.oks.packet.OneBotPacket

class RequireStatusPacket : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        echo?.let {
            return "{\"action\":\"get_status\",\"echo\":\"$it\"}"
        } ?: let {
            return "{\"action\":\"get_status\"}"
        }
    }
}