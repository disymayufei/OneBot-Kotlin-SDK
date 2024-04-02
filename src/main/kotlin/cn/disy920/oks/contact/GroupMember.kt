package cn.disy920.oks.contact

import cn.disy920.oks.annotation.Beta
import cn.disy920.oks.annotation.Undefined
import cn.disy920.oks.contact.GroupMember.Role.*
import cn.disy920.oks.message.chain.MessageChain
import cn.disy920.oks.packet.api.friend.SendTempMsgPacket
import cn.disy920.oks.packet.api.group.KickGroupMemberPacket
import cn.disy920.oks.packet.api.group.SetMemberNameCardPacket
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * 代表一个群成员
 * @param id QQ号
 * @param nickName 昵称
 * @param bot 所属机器人
 * @param age 年龄
 * @param sex 性别
 * @param group 所在群
 * @param level 等级
 * @param role 群权限
 * @param title 头衔
 * @see Member
 */
class GroupMember(
    override val id: Long,
    override val nickName: String,
    override val bot: Bot,
    override val age: Int,
    override val sex: Sex,
    val group: Group,
    val level: String,
    val role: Role,
    val title: String,
    memberNameCard: String
) : Member(id, nickName, bot, age, sex) {
    var nameCard: String = memberNameCard
        set(value) {
            changeNameCard(value)
            field = value
        }

    companion object {
        /**
         * 从JSON Node中解析出群成员
         */
        fun parseJson(json: ObjectNode, group: Group, bot: Bot): GroupMember {
            val id = json["user_id"]?.asLong() ?: throw IllegalArgumentException("Cannot generate group member from JSON: User ID is null")
            val nickName = json["nickname"]?.asText() ?: ""
            val nameCard = json["card"]?.asText() ?: ""
            val age = json["age"]?.asInt() ?: 0
            val sex = when (json["sex"]?.asText()) {
                "male" -> Sex.MALE
                "female" -> Sex.FEMALE
                else -> Sex.UNKNOWN
            }
            val level = json["level"]?.asText() ?: "0"
            val role = when (json["role"]?.asText()) {
                "owner" -> OWNER
                "admin" -> ADMIN
                "member" -> MEMBER
                else -> UNKNOWN
            }
            val title = json["title"]?.asText() ?: ""

            return GroupMember(id, nickName, bot, age, sex, group, level, role, title, nameCard)
        }

        fun getEmptyGroupMember(group: Group, userId: Long, bot: Bot): GroupMember {
            return GroupMember(
                userId,
                "",
                bot,
                0,
                Sex.UNKNOWN,
                group,
                "0",
                UNKNOWN,
                "",
                ""
            )
        }
    }

    /**
     * 修改群名片
     * @param nameCard 新的群名片
     */
    fun changeNameCard(nameCard: String) {
        bot.getConnection().postMsg(SetMemberNameCardPacket(group.id, id, nameCard))
    }

    /**
     * 踢出该群员，并不将其纳入黑名单
     */
    fun kick() {
        kick(false)
    }

    /**
     * 踢出该群员
     * @param blacklist 是否纳入黑名单
     */
    fun kick(blacklist: Boolean) {
        bot.getConnection().postMsg(KickGroupMemberPacket(group.id, id, blacklist))
    }

    /**
     * 向该成员发送临时会话消息
     * @param messages 消息链
     * @see MessageChain
     * @see Beta
     */
    @Beta("LLOneBot暂时不支持发送临时会话消息，仅限@disymayufei的fork版支持")
    override fun sendMessage(messages: MessageChain) {
        bot.getConnection().postMsg(SendTempMsgPacket(group.id, id, messages))
    }

    /**
     * 群权限
     * @property OWNER 群主
     * @property ADMIN 管理员
     * @property MEMBER 普通成员
     * @property UNKNOWN 未知
     */
    enum class Role {
        OWNER, ADMIN, MEMBER, UNKNOWN
    }
}