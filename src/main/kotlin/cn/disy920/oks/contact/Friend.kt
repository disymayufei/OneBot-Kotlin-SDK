package cn.disy920.oks.contact

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * 代表一个QQ好友
 * @param id 好友的QQ号
 * @param nickName 好友的昵称
 * @param bot 好友所对应的机器人
 * @param remark 好友的备注
 * @see User
 */
class Friend(
    override val id: Long,
    override val nickName: String,
    override val bot: Bot,
    val remark: String
) : User(id, nickName, bot) {
    companion object {
        /**
         * 从JSON中解析好友信息
         * @param json JSON对象
         * @param bot 好友所对应的机器人
         * @return 好友对象
         * @throws IllegalArgumentException 如果JSON中的user_id为null
         */
        fun parseJson(json: ObjectNode, bot: Bot) : Friend {
            val id = json["user_id"]?.asLong() ?: throw IllegalArgumentException("Cannot generate friend from JSON: User ID is null")
            val nickName = json["nickname"]?.asText() ?: ""
            val remark = json["remark"]?.asText() ?: nickName
            return Friend(id, nickName, bot, remark)
        }

        /**
         * 获取一个空的好友对象
         * @param bot 好友所对应的机器人
         * @param userId 好友的QQ号
         * @return 空的好友对象
         */
        fun getEmptyFriend(bot: Bot, userId: Long) : Friend {
            return Friend(userId, "", bot, "")
        }
    }
}