package cn.disy920.okapi.contact

import cn.disy920.okapi.message.Message
import cn.disy920.okapi.message.PlainMessage
import cn.disy920.okapi.message.chain.MessageChain
import cn.disy920.okapi.network.connection.BotConnection
import cn.disy920.okapi.packet.api.friend.SendPrivateMsgPacket
import cn.disy920.okapi.packet.api.group.RequireGroupMemberInfoPacket
import cn.disy920.okapi.packet.api.group.RequireGroupMemberListPacket
import cn.disy920.okapi.packet.api.group.SendGroupMsgPacket
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

class Group(
    override val id: Long,
    val bot: Bot
) : Contact {
    private var members: List<GroupMember>? = null

    /**
     * 获取群中的群成员，同时不使用本地缓存
     * @param id QQ号
     * @return 群成员
     */
    fun getMember(id: Long): GroupMember? {
        return getMember(id, false)
    }

    /**
     * 获取群中的群成员
     * @param id QQ号
     * @param useCache 是否使用缓存
     * @return 群成员
     */
    fun getMember(id: Long, useCache: Boolean): GroupMember? {
        return if (!useCache || members == null) {
            val memberInfoStr = bot.getConnection().postMsgWithResponse(
                RequireGroupMemberInfoPacket(this.id, id)
            )

            return GroupMember.parseJson(
                BotConnection.objectMapper.readTree(memberInfoStr) as ObjectNode,
                this,
                bot
            )
        }
        else {
            members!!.find { it.id == id }
        }
    }

    /**
     * 获取群成员列表，同时不使用本地缓存
     * @return 群成员列表
     */
    fun getMembers(): List<GroupMember> {
        return getMembers(false)
    }

    /**
     * 获取群成员列表
     * @param useCache 是否使用缓存
     * @return 群成员列表
     */
    fun getMembers(useCache: Boolean): List<GroupMember> {
        if (!useCache || members == null) {
            val memberList = ArrayList<GroupMember>()
            val membersInfoStr = bot.getConnection().postMsgWithResponse(
                RequireGroupMemberListPacket(id)
            )

            val membersInfo = BotConnection.objectMapper.readValue(membersInfoStr, ObjectNode::class.java)
            for (memberNode in membersInfo["data"]) {
                if (memberNode !is ObjectNode) continue
                memberList.add(GroupMember.parseJson(memberNode, this, bot))
            }

            this.members = ArrayList(memberList)

            return memberList
        }
        else {
            return members!!
        }
    }

    /**
     * 判断是否包含某个群成员，同时不使用本地缓存
     * @param id QQ号
     * @return 是否包含
     */
    fun contains(id: Long): Boolean {
        return contains(id, false)

    }

    /**
     * 判断是否包含某个群成员
     * @param id QQ号
     * @param useCache 是否使用缓存
     * @return 群成员列表
     */
    fun contains(id: Long, useCache: Boolean): Boolean {
        return if (!useCache || members == null) {
            getMembers().any { it.id == id }
        } else {
            members!!.any { it.id == id }
        }
    }

    /**
     * 发送群聊消息
     * @param message 待发送的消息
     */
    fun sendMessage(message: String) {
        sendMessage(PlainMessage(message))
    }

    /**
     * 发送群聊消息
     * @param message 待发送的消息
     */
    fun sendMessage(message: Message) {
        sendMessage(MessageChain(MessageChain.NEW_MSG_ID, listOf(message)))
    }

    /**
     * 发送群聊消息
     * @param messages 待发送的消息链
     */
    fun sendMessage(messages: MessageChain) {
        bot.getConnection().postMsg(SendGroupMsgPacket(id, messages))
    }
}