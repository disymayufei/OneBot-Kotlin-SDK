package cn.disy920.oks.packet.api.message

import cn.disy920.oks.annotation.Undefined
import cn.disy920.oks.network.connection.BotConnection.Companion.pack
import cn.disy920.oks.packet.OneBotPacket

class RequireRecordInfoPacket(
    val file: String,
    val outFormat: FormatType = FormatType.DEFAULT,
) : OneBotPacket {
    @Undefined("LLOneBot暂时不支持进行格式转换，该参数可能不会生效，建议仅使用DEFAULT")
    enum class FormatType(
        val type: String
    ) {
        MP3("mp3"),
        AMR("amr"),
        WMA("wma"),
        M4A("m4a"),
        SPX("spx"),
        OGG("ogg"),
        WAV("wav"),
        FLAC("flac"),
        DEFAULT("")  // 不进行转换
    }

    override fun toJsonString(echo: String?): String {
        if (outFormat != FormatType.DEFAULT) {
            return pack(
                "get_record",
                mapOf(
                    "file" to file,
                    "out_format" to outFormat.type
                ),
                echo
            ).toString()
        }
        else {
            return pack(
                "get_record",
                mapOf(
                    "file" to file
                ),
                echo
            ).toString()
        }
    }
}