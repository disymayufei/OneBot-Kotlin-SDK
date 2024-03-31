package cn.disy920.oks.message

import cn.disy920.oks.message.tag.Receivable
import cn.disy920.oks.message.tag.Sendable
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.util.*

/**
 * 表示一个图片消息
 * @see Message
 */
class Image : Message, Sendable, Receivable {
    private val imageBinary: ByteArray
    private val base64Pattern = Regex("^[a-zA-Z0-9+/]*={0,2}\$")
    constructor(inputStream: InputStream) {
        imageBinary = try {
            inputStream.readAllBytes()
        }
        catch (e: Exception) {
            throw IllegalArgumentException("An exception occurred while encoding the image", e)
        }
    }

    constructor(bytes: ByteArray) {
        imageBinary = bytes
    }

    constructor(base64: String) {
        if (base64Pattern.matches(base64)) {
            imageBinary = Base64.getDecoder().decode(base64)
        }
        else {
            throw IllegalArgumentException("Not a valid base64")
        }
    }

    constructor(file: File) {
        imageBinary = try {
            Files.readAllBytes(file.toPath())
        }
        catch (e: Exception) {
            throw IllegalArgumentException("An exception occurred while encoding the image", e)
        }
    }

    constructor(url: URL) {
        url.openConnection().getInputStream().use {
            imageBinary = it.readAllBytes()
        }
    }

    fun convertToUrl(): String {
        return "base64://${Base64.getEncoder().encodeToString(imageBinary)}"
    }

    override fun contentToString(): String {
        return "[图片]"
    }
}