package cn.disy920.okapi.message

import cn.disy920.okapi.message.tag.Receivable
import cn.disy920.okapi.message.tag.Sendable

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
}