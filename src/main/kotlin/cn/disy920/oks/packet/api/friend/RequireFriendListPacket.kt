package cn.disy920.oks.packet.api.friend

import cn.disy920.oks.packet.OneBotPacket

class RequireFriendListPacket : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        echo?.let {
            return "{\"action\":\"get_friend_list\",\"echo\":\"$echo\"}"
        } ?: let {
            return "{\"action\":\"get_friend_list\"}"
        }

    }
}