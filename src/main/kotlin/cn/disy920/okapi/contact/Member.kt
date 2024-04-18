package cn.disy920.okapi.contact

/**
 * 代表一个成员，可能是位于群中的群成员，也可能是来自于某个群临时会话的临时会话成员
 * @param id QQ号
 * @param nickName 昵称
 * @param bot 所属机器人
 * @param age 年龄
 * @param sex 性别
 * @see User
 */
open class Member(
    override val id: Long,
    override val nickName: String,
    override val bot: Bot,
    open val age: Int,
    open val sex: Sex
) : User(id, nickName, bot) {
    enum class Sex {
        MALE, FEMALE, UNKNOWN
    }
}