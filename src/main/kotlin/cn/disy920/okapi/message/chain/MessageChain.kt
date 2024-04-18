package cn.disy920.okapi.message.chain

import cn.disy920.okapi.message.*
import cn.disy920.okapi.message.enums.Faces
import cn.disy920.okapi.message.tag.Sendable
import cn.disy920.okapi.network.connection.BotConnection
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.URL

/**
 * 消息链，用于存储一系列基本消息
 * @param id 消息链ID，用于引用回复消息，如果为-1则表示这是一条尚未被发送的新消息
 */
class MessageChain : ArrayList<Message> {
    companion object {
        const val NEW_MSG_ID = -1  // 新消息ID，用于标识尚未发送的消息

        /**
         * 从JSON数据节点对象中反序列化出消息链
         * @param data JSON数据节点
         * @param id 消息链ID
         * @return 反序列化出的消息链
         * @see MessageChain
         */
        fun parseJson(data: ArrayNode?, id: Int): MessageChain {
            val chain = MessageChain(id)

            if (data == null || data.isNull || data.isEmpty) {
                return chain
            }

            for (node in data) {
                if (node !is ObjectNode) {
                    return chain
                }

                val type: String = (node["type"]?.let { typeNode ->
                    if (typeNode.isTextual) {
                        return@let typeNode.asText()
                    }
                    return@let null
                }) ?: return chain

                val body: ObjectNode = (node["data"]?.let { dataNode ->
                    if (dataNode.isObject) {
                        return@let dataNode as ObjectNode
                    }
                    return@let null
                }) ?: return chain

                when (type) {
                    "text" -> {
                        body["text"]?.let { textNode ->
                            if (textNode.isTextual) {
                                chain.add(PlainMessage(textNode.asText()))
                            }
                        }
                    }

                    "at" -> {
                        body["qq"]?.let { atNode ->
                            if (!atNode.isTextual) {
                                return@let
                            }

                            val target = atNode.asText()
                            if (target == "all") {
                                chain.add(At(At.ALL))
                            }
                            else {
                                chain.add(At(target.toLong()))
                            }
                        }
                    }

                    "face" -> {
                        body["id"]?.let { faceNode ->
                            if (!faceNode.isTextual) {
                                return@let
                            }

                            val faceId = faceNode.asText().toInt()
                            chain.add(Faces.getFaceByCode(faceId) ?: Face(faceId, "表情"))
                        }
                    }

                    "image" -> {
                        if (body.has("url")) {
                            body["url"]?.let { urlNode ->
                                if (urlNode.isTextual) {
                                    val url = urlNode.asText()
                                    if (url.startsWith("http")) {
                                        chain.add(Image(URL(url)))
                                    }
                                    else if (url.startsWith("base64://")) {
                                        chain.add(Image(url.substring(9)))
                                    }
                                }
                            }
                        }
                    }

                    "record" -> {
                        body["file"]?.asText()?.let { file ->
                            chain.add(Record(file, Record.DataType.PATH))
                        }
                    }
                }
            }

            return chain
        }
    }

    val messageId: Int  // 消息链ID
    var sendable: Boolean = true  // 消息链是否可以被发送

    /**
     * 构造一个消息链
     * @param id 消息链ID
     * @param messages 消息列表
     * @see MessageChain
     */
    constructor(id: Int, messages: Collection<Message>) : super(messages) {
        this.messageId = id
    }

    /**
     * 构造一个空的消息链，用于构造一个待发送的消息
     * @see MessageChain
     */
    constructor() : super() {
        this.messageId = NEW_MSG_ID
    }

    /**
     * 用指定的ID构造一个消息链
     * @param id 消息链ID
     * @see MessageChain
     */
    private constructor(id: Int) : super() {
        this.messageId = id
    }

    /**
     * 将消息链转换为可读的字符串
     * 无法被直接转化为字符串的消息（如图片，动画表情等）将依照QQ的格式进行显示（如：&#91;图片&#93;， &#91;动画表情&#93;)
     * @return 可读的字符串
     */
    fun contentToString(): String {
        return this.joinToString(prefix = "[", postfix = "]") {
            it.contentToString()
        }
    }

    /**
     * 将消息链序列化为JSON数据节点对象
     * @return JSON数据节点对象
     */
    fun serializeToJson(): ArrayNode {
        val arrayNode = BotConnection.nodeFactory.arrayNode()

        for (message in this) {
            when (message) {
                is PlainMessage -> {
                    arrayNode.addPOJO(
                        mapOf(
                            "type" to "text",
                            "data" to mapOf(
                                "text" to message.getPlainText()
                            )
                        )
                    )
                }

                is At -> {
                    arrayNode.addPOJO(
                        mapOf(
                            "type" to "at",
                            "data" to mapOf(
                                "qq" to if(message.target == At.ALL) "all" else message.target.toString()
                            )
                        )
                    )
                }

                is Image -> {
                    arrayNode.addPOJO(
                        mapOf(
                            "type" to "image",
                            "data" to mapOf(
                                "file" to message.convertToUrl()
                            )
                        )
                    )
                }

                is Record -> {
                    arrayNode.addPOJO(
                        mapOf(
                            "type" to "record",
                            "data" to mapOf(
                                "file" to message.convertToUrl()
                            )
                        )
                    )
                }

                is Face -> {
                    arrayNode.addPOJO(
                        mapOf(
                            "type" to "face",
                            "data" to mapOf(
                                "id" to message.code
                            )
                        )
                    )
                }

                is QuoteReply -> {
                    arrayNode.addPOJO(
                        mapOf(
                            "type" to "reply",
                            "data" to mapOf(
                                "id" to message.messageId
                            )
                        )
                    )
                }
            }
        }

        return arrayNode
    }
    override fun add(element: Message): Boolean {
        val bl = super.add(element)
        if (sendable && element !is Sendable) {
            sendable = false
        }

        return bl
    }
}