package cn.disy920.okapi.message

import cn.disy920.okapi.message.tag.Receivable
import cn.disy920.okapi.message.tag.Sendable

/**
 * 表示一个纯文本消息，内部只能包含文字，
 * 注意，同字符串对象不同，该对象不具有不可变性，因此不要将其作为HashMap的Key
 * @param text 文本内容
 * @see Message
 */
class PlainMessage : Message, Sendable, Receivable {
    private val buffer: StringBuffer

    constructor(text: String) {
        buffer = StringBuffer(text)
    }

    constructor(text: CharSequence) {
        buffer = StringBuffer(text)
    }

    fun append(text: Any): PlainMessage {
        buffer.append(text)
        return this
    }

    fun append(c: Char): PlainMessage {
        buffer.append(c)
        return this
    }

    fun append(i: Int): PlainMessage {
        buffer.append(i)
        return this
    }

    fun getPlainText(): String {
        return contentToString()
    }

    override fun contentToString(): String {
        return buffer.toString()
    }

    override fun toString(): String {
        return "[PlainMessage: $buffer]"
    }
}