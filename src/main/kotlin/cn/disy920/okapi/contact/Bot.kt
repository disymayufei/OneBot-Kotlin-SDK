package cn.disy920.okapi.contact

import cn.disy920.okapi.network.connection.BotConnection
import cn.disy920.okapi.network.exception.BotNotOnlineException
import cn.disy920.okapi.network.exception.TimeoutException
import cn.disy920.okapi.packet.api.friend.RequireFriendListPacket
import cn.disy920.okapi.packet.api.group.RequireGroupMemberInfoPacket
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.concurrent.ConcurrentHashMap

/**
 * 代表一个OneBot机器人
 * 该对象可以安全地作为HashMap的key
 * @param id 机器人的QQ号
 * @see UserOrBot
 */
class Bot private constructor(
    override val id: Long,
    @Volatile
    private var connection: BotConnection?
) : UserOrBot() {

    private val friendList = ArrayList<Friend>()

    init {
        if (id < 10000) {
            throw IllegalArgumentException("机器人QQ号值应大于10000")
        }
    }

    companion object {
        private val botPool = ConcurrentHashMap<Long, Bot>()

        @Synchronized
        fun registerNewBot(botId: Long): Bot {
            val bot = Bot(botId, null)
            botPool[botId] = bot
            return bot
        }

        @Synchronized
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
        connection ?: throw BotNotOnlineException("机器人 $id 不在线")

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
        } ?: throw TimeoutException("机器人 $id 获取群 $userId 成员超时")
    }

    /**
     * 获取好友信息
     * @param userId 待获取信息的用户QQ号
     * @param useCache 是否使用缓存
     * @return 好友信息
     * @see Friend
     */
    fun getFriend(userId: Long, useCache: Boolean) : Friend? {
        connection ?: throw BotNotOnlineException("机器人 $id 不在线")

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
                } ?: throw TimeoutException("机器人 $id 从服务器拉取好友列表超时")
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
            } ?: throw TimeoutException("机器人 $id 从服务器拉取好友列表超时")
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
            throw BotNotOnlineException("机器人 $id 不在线")
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