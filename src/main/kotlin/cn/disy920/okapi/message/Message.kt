package cn.disy920.okapi.message

/**
 * 代表OneBot的消息，用于描述OneBot所能发送或接收的消息，所有可发送的消息必须实现此接口
 */
interface Message {
    /**
     * 将任何消息转化为字符串的形式
     * 无法被直接转化为字符串的消息（如图片，动画表情等）将依照QQ的格式进行显示（如：&#91;图片&#93;， &#91;动画表情&#93;)
     * @return 被转化为字符串的消息
     */
    fun contentToString(): String
}