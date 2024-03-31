package cn.disy920.oks.contact

import cn.disy920.oks.network.connection.BotConnection
import cn.disy920.oks.network.exception.BotNotOnlineException
import cn.disy920.oks.network.exception.TimeoutException
import cn.disy920.oks.packet.api.friend.RequireFriendListPacket
import cn.disy920.oks.packet.api.group.RequireGroupMemberInfoPacket
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * 代表一个OneBot机器人
 * 该对象可以安全地作为HashMap的key
 * @param id 机器人的QQ号
 * @see UserOrBot
 */
class Bot private constructor(
    override val id: Long,
    private var connection: BotConnection?
) : UserOrBot() {

    private val friendList = ArrayList<Friend>()

    init {
        if (id < 10000) {
            throw IllegalArgumentException("Bot id should be greater than 10000")
        }
    }

    companion object {
        private val botPool = HashMap<Long, Bot>()

        fun registerNewBot(botId: Long): Bot {
            val bot = Bot(botId, null)
            botPool[botId] = bot
            return bot
        }

        fun updateConnection(botId: Long, botConnection: BotConnection) {
            botPool[botId]?.updateConnection(botConnection)
        }

        fun unregisterBot(botId: Long) {
            botPool.remove(botId)
        }

        fun getBot(botId: Long): Bot? {
            return botPool[botId]
        }
    }

    /**
     * 获取群成员信息
     * @param group 待获取信息的群
     * @param userId 待获取信息的用户QQ号
     * @param useCache 是否使用缓存
     * @return 群成员信息
     */
    fun getGroupMember(group: Group, userId: Long, useCache: Boolean) : GroupMember? {
        connection ?: throw BotNotOnlineException("Bot $id is not online")

        val response = connection!!.postMsgWithResponse(
            RequireGroupMemberInfoPacket(group.id, userId, useCache)
        )

        response?.let {
            val responseJson = BotConnection.objectMapper.readTree(it)
            responseJson?.let {
                if (responseJson is ObjectNode) {
                    return GroupMember.parseJson(responseJson, group, this)
                }
                else {
                    return null
                }
            }
        } ?: throw TimeoutException("Bot $id reached the upper limit of waiting time when get group member $userId timeout")
    }

    /**
     * 获取好友信息
     * @param userId 待获取信息的用户QQ号
     * @param useCache 是否使用缓存
     * @return 好友信息
     * @see Friend
     */
    fun getFriend(userId: Long, useCache: Boolean) : Friend? {
        connection ?: throw BotNotOnlineException("Bot $id is not online")

        if (useCache) {
            if (friendList.isEmpty()) {
                val response = connection!!.postMsgWithResponse(
                    RequireFriendListPacket()
                )

                response?.let {
                    val responseJson = BotConnection.objectMapper.readTree(it)
                    responseJson?.let {
                        var target: Friend? = null
                        if (responseJson is ArrayNode) {
                            for (i in 0 until responseJson.size()) {
                                try {
                                    val friend = Friend.parseJson(responseJson[i] as ObjectNode, this)
                                    if (friend.id == userId) {
                                        target = friend
                                    }
                                    friendList.add(friend)
                                }
                                catch (e: Exception) {
                                    continue
                                }
                            }

                            return target
                        }
                        else {
                            return null
                        }
                    }
                } ?: throw TimeoutException("Bot $id reached the upper limit of waiting time when get friend list from server")
            }
            else {
                return friendList.find { it.id == userId }
            }
        }
        else {
            val response = connection!!.postMsgWithResponse(
                RequireFriendListPacket()
            )

            response?.let {
                val responseJson = BotConnection.objectMapper.readTree(it)
                responseJson?.let {
                    var target: Friend? = null
                    if (responseJson is ArrayNode) {
                        friendList.clear()

                        for (i in 0 until responseJson.size()) {
                            try {
                                val friend = Friend.parseJson(responseJson[i] as ObjectNode, this)
                                if (friend.id == userId) {
                                    target = friend
                                }
                                friendList.add(friend)
                            }
                            catch (e: Exception) {
                                continue
                            }
                        }

                        return target
                    }
                    else {
                        return null
                    }
                }
            } ?: throw TimeoutException("Bot $id reached the upper limit of waiting time when get friend list from server")
        }
    }

    /**
     * 更新Bot对象与OneBot端连接
     * @param botConnection 新的OneBot端连接
     * @see BotConnection
     */
    fun updateConnection(botConnection: BotConnection) {
        connection = botConnection
    }

    fun getConnection(): BotConnection {
        if (!isOnline()) {
            throw BotNotOnlineException("Bot $id is not online")
        }

        return connection!!
    }

    fun isOnline(): Boolean {
        return connection != null && connection!!.isOpen()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bot
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}