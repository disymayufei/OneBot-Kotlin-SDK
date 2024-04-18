package cn.disy920.okapi.message

import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.message.tag.Receivable
import cn.disy920.okapi.message.tag.Sendable
import cn.disy920.okapi.network.connection.BotConnection
import cn.disy920.okapi.packet.api.message.RequireRecordInfoPacket
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.util.*

class Record : Message, Sendable, Receivable {
    private val recordBinary: ByteArray?
    private val recordPath: String?
    private val base64Pattern = Regex("^[a-zA-Z0-9+/]*={0,2}\$")

    enum class DataType {
        BASE64, PATH
    }

    constructor(inputStream: InputStream) {
        recordPath = null
        recordBinary = try {
            inputStream.readAllBytes()
        }
        catch (e: Exception) {
            throw IllegalArgumentException("An exception occurred while encoding the record", e)
        }
    }

    constructor(bytes: ByteArray) {
        recordPath = null
        recordBinary = bytes
    }

    constructor(data: String, dataType: DataType) {
        when (dataType) {
            DataType.BASE64 -> {
                if (base64Pattern.matches(data)) {
                    recordBinary = Base64.getDecoder().decode(data)
                    recordPath = null
                }
                else {
                    throw IllegalArgumentException("Not a valid base64")
                }
            }

            DataType.PATH -> {
                recordBinary = null
                recordPath = data
            }
        }
    }

    constructor(file: File) {
        recordPath = null
        recordBinary = try {
            Files.readAllBytes(file.toPath())
        }
        catch (e: Exception) {
            throw IllegalArgumentException("An exception occurred while encoding the record", e)
        }
    }

    constructor(url: URL) {
        recordPath = null
        url.openConnection().getInputStream().use {
            recordBinary = it.readAllBytes()
        }
    }

    fun convertToUrl(): String {
        return if (recordPath != null) {
            "file://${recordPath}"
        } else {
            "base64://${Base64.getEncoder().encodeToString(recordBinary)}"
        }
    }

    fun getBinary(fromBot: Bot): ByteArray? {
        return recordBinary ?: let {
            recordPath ?: return@let null
            val recordPacket = BotConnection.objectMapper.readTree(fromBot.getConnection().postMsgWithResponse(RequireRecordInfoPacket(recordPath)))
            val base64 = recordPacket["data"]["base64"]?.asText()
            return@let base64?.let {
                Base64.getDecoder().decode(base64)
            }
        }
    }

    override fun contentToString(): String {
        return "[语音]"
    }
}