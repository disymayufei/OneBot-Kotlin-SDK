package cn.disy920.okapi.network.connection

import cn.disy920.okapi.bot.OneBot
import cn.disy920.okapi.contact.*
import cn.disy920.okapi.event.message.GroupMessageEvent
import cn.disy920.okapi.event.message.PrivateMessageEvent
import cn.disy920.okapi.event.meta.HeartbeatEvent
import cn.disy920.okapi.event.meta.lifecycle.BotDisableEvent
import cn.disy920.okapi.event.meta.lifecycle.BotEnableEvent
import cn.disy920.okapi.event.meta.lifecycle.NewWebsocketConnectedEvent
import cn.disy920.okapi.event.notice.admin.AdminDecreaseEvent
import cn.disy920.okapi.event.notice.admin.AdminIncreaseEvent
import cn.disy920.okapi.event.notice.friend.FriendPokeEvent
import cn.disy920.okapi.event.notice.group.GroupMemberDecreaseEvent
import cn.disy920.okapi.event.notice.group.GroupMemberIncreaseEvent
import cn.disy920.okapi.event.notice.group.GroupMuteEvent
import cn.disy920.okapi.event.notice.group.GroupPokeEvent
import cn.disy920.okapi.event.notice.recall.FriendRecallEvent
import cn.disy920.okapi.event.notice.recall.GroupRecallEvent
import cn.disy920.okapi.event.request.FriendRequestEvent
import cn.disy920.okapi.event.request.GroupRequestEvent
import cn.disy920.okapi.message.chain.MessageChain
import cn.disy920.okapi.packet.OneBotPacket
import cn.disy920.okapi.packet.api.bot.RequireLoginInfoPacket
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

/**
 * 机器人连接接口，代表一个与OneBot端的连接
 * @see Bot
 */
interface BotConnection {

    val type: BotConnectionType
    var botId: Long?  // 机器人的QQ号, 初始值应恒为null

    /**
     * 机器人连接类型
     */
    enum class BotConnectionType {
        HTTP, HTTP_POST, WEBSOCKET, WEBSOCKET_REVERSE
    }

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper()
        val nodeFactory: JsonNodeFactory = objectMapper.nodeFactory
        private val logger = LoggerFactory.getLogger("Bot Connection")

        /**
         * 封装消息
         * @param header 包类型
         * @param params 参数
         * @return 封装后的消息
         */
        @JvmStatic
        fun pack(header: String, params: Map<String, Any?>, echo: String? = null): ObjectNode {
            return objectMapper.valueToTree(
                mapOf(
                    "action" to header,
                    "params" to params,
                    "echo" to (echo ?: "")
                )
            )
        }

        /**
         * 处理机器人事件
         * @param params 事件JSON参数节点
         */
        @JvmStatic
        fun processEvent(params: ObjectNode) {
            val postType = params.get("post_type").asText()
            val time = params.get("time").asLong()

            Bot.getBot(params.get("self_id").asLong())?.let { bot ->
                when (postType) {
                    // 消息事件
                    "message" -> {
                        val messageType = params.get("message_type").asText()
                        val messageId = params.get("message_id").asInt()
                        val messageChainArray = params.get("message") as ArrayNode

                        val messageChain = MessageChain.parseJson(messageChainArray, messageId)
                        when (messageType) {
                            // 私聊消息
                            "private" -> {
                                val friend = Friend.parseJson(
                                    params.get("sender") as ObjectNode,
                                    bot
                                )
                                OneBot.eventManager.callEvent(
                                    bot,
                                    PrivateMessageEvent(
                                        time,
                                        bot,
                                        messageChain,
                                        friend
                                    )
                                )
                            }

                            // 群聊消息
                            "group" -> {
                                val group = Group(params.get("group_id").asLong(), bot)
                                val member = GroupMember.parseJson(
                                    params.get("sender") as ObjectNode,
                                    group,
                                    bot
                                )

                                OneBot.eventManager.callEvent(
                                    bot,
                                    GroupMessageEvent(
                                        time,
                                        bot,
                                        messageChain,
                                        member,
                                        group
                                    )
                                )
                            }
                        }
                    }

                    // 通知事件
                    "notice" -> {
                        val noticeType = params.get("notice_type").asText()
                        when (noticeType) {
                            // 群管理员变动事件
                            "group_admin" -> {
                                val set = (params.get("sub_type").asText() == "set")
                                val group = Group(params.get("group_id").asLong(), bot)
                                val member = bot.getGroupMember(group, params.get("user_id").asLong(), false) ?: GroupMember.getEmptyGroupMember(group, params.get("user_id").asLong(), bot)

                                if (set) {
                                    // 群管理员增加事件
                                    OneBot.eventManager.callEvent(
                                        bot,
                                        AdminIncreaseEvent(
                                            time,
                                            bot,
                                            group,
                                            member
                                        )
                                    )
                                }
                                else {
                                    // 群管理员减少事件
                                    OneBot.eventManager.callEvent(
                                        bot,
                                        AdminDecreaseEvent(
                                            time,
                                            bot,
                                            group,
                                            member
                                        )
                                    )
                                }
                            }

                            // 群成员减少事件
                            "group_decrease" -> {
                                val group = Group(params.get("group_id").asLong(), bot)
                                val member = bot.getGroupMember(group, params.get("user_id").asLong(), false) ?: GroupMember.getEmptyGroupMember(group, params.get("user_id").asLong(), bot)
                                val type = when (params.get("sub_type").asText()) {
                                    "leave" -> {
                                        GroupMemberDecreaseEvent.Type.LEAVE
                                    }
                                    "kick" -> {
                                        GroupMemberDecreaseEvent.Type.KICK
                                    }
                                    "kick_me" -> {
                                        GroupMemberDecreaseEvent.Type.KICK_ME
                                    }
                                    else -> {
                                        GroupMemberDecreaseEvent.Type.LEAVE
                                    }
                                }
                                OneBot.eventManager.callEvent(
                                    bot,
                                    GroupMemberDecreaseEvent(
                                        time,
                                        bot,
                                        group,
                                        member,
                                        params.get("operator_id").asLong(),
                                        type
                                    )
                                )
                            }

                            // 群成员增加事件
                            "group_increase" -> {
                                val group = Group(params.get("group_id").asLong(), bot)
                                val member = bot.getGroupMember(group, params.get("user_id").asLong(), false) ?: GroupMember.getEmptyGroupMember(group, params.get("user_id").asLong(), bot)
                                val type = when (params.get("sub_type").asText()) {
                                    "approve" -> {
                                        GroupMemberIncreaseEvent.Type.APPROVE
                                    }
                                    "invite" -> {
                                        GroupMemberIncreaseEvent.Type.INVITE
                                    }
                                    else -> {
                                        GroupMemberIncreaseEvent.Type.APPROVE
                                    }
                                }
                                OneBot.eventManager.callEvent(
                                    bot,
                                    GroupMemberIncreaseEvent(
                                        time,
                                        bot,
                                        group,
                                        member,
                                        params.get("operator_id").asLong(),
                                        type
                                    )
                                )
                            }

                            // 群禁言事件
                            "group_ban" -> {
                                val group = Group(params.get("group_id").asLong(), bot)
                                val member = bot.getGroupMember(group, params.get("user_id").asLong(), false) ?: GroupMember.getEmptyGroupMember(group, params.get("user_id").asLong(), bot)
                                OneBot.eventManager.callEvent(
                                    bot,
                                    GroupMuteEvent(
                                        time,
                                        bot,
                                        group,
                                        member,
                                        params.get("operator_id").asLong(),
                                        params.get("duration").asLong()
                                    )
                                )
                            }

                            // 群消息撤回事件
                            "group_recall" -> {
                                val group = Group(params.get("group_id").asLong(), bot)
                                val member = bot.getGroupMember(group, params.get("user_id").asLong(), false) ?: GroupMember.getEmptyGroupMember(group, params.get("user_id").asLong(), bot)
                                OneBot.eventManager.callEvent(
                                    bot,
                                    GroupRecallEvent(
                                        time,
                                        bot,
                                        group,
                                        member,
                                        params.get("message_id").asLong(),
                                        params.get("operator_id").asLong(),
                                    )
                                )
                            }

                            // 好友消息撤回事件
                            "friend_recall" -> {
                                val friend = bot.getFriend(params.get("user_id").asLong(), true) ?: Friend.getEmptyFriend(bot, params.get("user_id").asLong())
                                OneBot.eventManager.callEvent(
                                    bot,
                                    FriendRecallEvent(
                                        time,
                                        bot,
                                        friend,
                                        params.get("message_id").asLong()
                                    )
                                )
                            }

                            // 戳一戳事件
                            "poke" -> {
                                // 群聊戳一戳事件
                                if (params.has("group_id")) {
                                    val group = Group(params.get("group_id").asLong(), bot)
                                    val member = bot.getGroupMember(group, params.get("user_id").asLong(), false) ?: GroupMember.getEmptyGroupMember(group, params.get("user_id").asLong(), bot)
                                    OneBot.eventManager.callEvent(
                                        bot,
                                        GroupPokeEvent(
                                            time,
                                            bot,
                                            group,
                                            member,
                                            params.get("target_id").asLong()
                                        )
                                    )
                                }
                                // 好友戳一戳事件
                                else {
                                    val friend = bot.getFriend(params.get("user_id").asLong(), true) ?: Friend.getEmptyFriend(bot, params.get("user_id").asLong())
                                    OneBot.eventManager.callEvent(
                                        bot,
                                        FriendPokeEvent(
                                            time,
                                            bot,
                                            friend,
                                            params.get("target_id").asLong()
                                        )
                                    )

                                }
                            }
                        }
                    }

                    "request" -> {
                        val requestType = params.get("request_type").asText()
                        when (requestType) {
                            // 好友请求事件
                            "friend" -> {
                                val stranger = Stranger.getEmptyStranger(bot, params.get("user_id").asLong())
                                OneBot.eventManager.callEvent(
                                    bot,
                                    FriendRequestEvent(
                                        time,
                                        bot,
                                        params.get("flag").asText(),
                                        stranger,
                                        params.get("comment").asText()
                                    )

                                )
                            }

                            // 群请求事件
                            "group" -> {
                                val stranger = Stranger.getEmptyStranger(bot, params.get("user_id").asLong())
                                val group = Group(params.get("group_id").asLong(), bot)
                                val type = when (params.get("sub_type").asText()) {
                                    "add" -> {
                                        GroupRequestEvent.Type.ADD
                                    }
                                    "invite" -> {
                                        GroupRequestEvent.Type.INVITE
                                    }
                                    else -> {
                                        GroupRequestEvent.Type.ADD
                                    }
                                }
                                OneBot.eventManager.callEvent(
                                    bot,
                                    GroupRequestEvent(
                                        time,
                                        bot,
                                        params.get("flag").asText(),
                                        group,
                                        stranger,
                                        params.get("comment").asText(),
                                        type
                                    )
                                )
                            }
                        }
                    }

                    "meta_event" -> {
                        val metaType = params.get("meta_event_type").asText()
                        when (metaType) {
                            // 生命周期事件
                            "lifecycle" -> {
                                val subType = params.get("sub_type").asText()
                                when (subType) {
                                    // 新的Websocket连接事件
                                    "connect" -> {
                                        OneBot.eventManager.callEvent(
                                            bot,
                                            NewWebsocketConnectedEvent(
                                                time,
                                                bot
                                            )
                                        )
                                    }

                                    // OneBot启用事件
                                    "enable" -> {
                                        OneBot.eventManager.callEvent(
                                            bot,
                                            BotEnableEvent(
                                                time,
                                                bot
                                            )
                                        )
                                    }

                                    // OneBot关闭事件
                                    "disable" -> {
                                        OneBot.eventManager.callEvent(
                                            bot,
                                            BotDisableEvent(
                                                time,
                                                bot
                                            )
                                        )
                                    }
                                }
                            }

                            // 心跳事件
                            "heartbeat" -> {
                                val statusObj = params.get("status") as ObjectNode
                                val status = HeartbeatEvent.HeartbeatStatus(
                                    statusObj.get("good").asBoolean(),
                                    statusObj.get("online").asBoolean()
                                )

                                OneBot.eventManager.callEvent(
                                    bot,
                                    HeartbeatEvent(
                                        time,
                                        bot,
                                        status,
                                        params.get("interval").asLong()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 发送消息包并等待响应
     * @param packet 待发送的消息包
     * @param timeOut 等待响应的超时时间
     * @return 响应消息
     * @see OneBotPacket
     */
    abstract fun postMsgWithResponse(packet: OneBotPacket, timeOut: Long = 3000): String?

    /**
     * 发送JSON体并等待响应
     * @param jsonNode 待发送的JSON体
     * @param timeOut 等待响应的超时时间
     * @return 响应消息
     */
    abstract fun postMsgWithResponse(jsonNode: JsonNode, timeOut: Long = 3000): String?

    /**
     * 发送消息包
     * @param jsonNode 待发送的JSON体
     */
    abstract fun postMsg(jsonNode: JsonNode)

    /**
     * 发送消息包
     * @param packet 待发送的消息包
     * @see OneBotPacket
     */
    abstract fun postMsg(packet: OneBotPacket)

    /**
     * 连接打开时调用
     * 任何子类均应调用此超方法，以确保连接可以被正确注册到机器人中
     */
    fun onOpen() {
        val connection = this

        CoroutineScope(Dispatchers.Default).launch {
            val loginInfo = postMsgWithResponse(RequireLoginInfoPacket(), 5000)
            loginInfo?.let {
                val loginInfoJson: ObjectNode = objectMapper.readValue(loginInfo, ObjectNode::class.java)
                val botId = loginInfoJson["data"]["user_id"].asLong()
                if (botId != 0L) {
                    connection.botId = botId
                    Bot.updateConnection(botId, connection)
                    logger.info("检测到来自机器人 $botId 的新连接!")
                }
            }
        }
    }

    /**
     * 连接关闭时调用
     * 任何子类均应调用此超方法，以确保连接断开时可以正确注销机器人
     */
    fun onClose() {
        botId?.let {
            Bot.unregisterBot(it)
        }
    }

    /**
     * 连接是否打开
     * @return 连接是否打开
     */
    abstract fun isOpen(): Boolean
}