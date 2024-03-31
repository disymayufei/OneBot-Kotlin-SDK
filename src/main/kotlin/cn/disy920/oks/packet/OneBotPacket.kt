package cn.disy920.oks.packet

/**
 * 代表一个OneBot协议包
 */
interface OneBotPacket {
    /**
     * 将包转换为JSON字符串
     * @param echo 用于标识本次请求的唯一ID
     * @return JSON字符串
     */
    fun toJsonString(echo: String? = null): String
}