package cn.disy920.okapi.packet.api.friend

import cn.disy920.okapi.packet.OneBotPacket

class RequireFriendListPacket : OneBotPacket {
    override fun toJsonString(echo: String?): String {
        echo?.let {
            return "{\"action\":\"get_friend_list\",\"echo\":\"$echo\"}"
        } ?: let {
            return "{\"action\":\"get_friend_list\"}"
        }

    }
}