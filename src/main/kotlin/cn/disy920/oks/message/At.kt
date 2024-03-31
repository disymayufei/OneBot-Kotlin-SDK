package cn.disy920.oks.message

import cn.disy920.oks.message.tag.Receivable
import cn.disy920.oks.message.tag.Sendable

/**
 * 表示一个At消息，用于描述At某个用户
 * @param target At的目标QQ号，-1代表At全体成员
 * @see Message
 */
@Suppress("MemberVisibilityCanBePrivate")
class At(val target: Long) : Message, Sendable, Receivable {
    companion object {
        const val ALL = -1L
    }

    override fun contentToString(): String {
        return "@$target"
    }

    override fun toString(): String {
        return "[At: $target]"
    }

    fun getDisplay(): String {
        TODO("尚未实现Display方法")
    }
}