package cn.disy920.oks.message

import cn.disy920.oks.message.tag.Receivable
import cn.disy920.oks.message.tag.Sendable

/**
 * 表示一段引用回复
 * 在一个MessageChain里，应最多仅出现一个引用回复，否则产生的结果是未定义的
 * @param messageId 引用的消息ID
 * @see Message
 */
class QuoteReply(val messageId: Int) : Message, Sendable, Receivable {
    override fun contentToString(): String {
        return ""
    }

}