package cn.disy920.oks.message.chain

import cn.disy920.oks.message.Message
import cn.disy920.oks.message.PlainMessage

/**
 * 用于构建消息链的构建器
 * @see MessageChain
 */
class MessageChainBuilder {
    private val chain: MessageChain = MessageChain()

    /**
     * 向消息链中追加一个纯文本消息，效果等同于追加一个[PlainMessage]
     * @param text 要追加的文本
     */
    fun append(text: String): MessageChainBuilder {
        chain.add(PlainMessage(text))
        return this
    }

    /**
     * 向消息链中追加一个消息
     * @param message 要追加的消息
     */
    fun append(message: Message): MessageChainBuilder {
        chain.add(message)
        return this
    }

    /**
     * 向消息链中追加一个对象，加入时会将对象转化为一个纯文字组成的字符串
     * @param o 要追加的对象
     */
    fun append(o: Any): MessageChainBuilder {
        return append(o.toString())
    }

    /**
     * 构建消息链
     * @return 构建的消息链
     * @see MessageChain
     */
    fun build(): MessageChain {
        return chain
    }
}