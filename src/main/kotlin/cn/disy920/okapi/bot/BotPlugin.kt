package cn.disy920.okapi.bot

import cn.disy920.okapi.config.Configuration
import cn.disy920.okapi.config.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

abstract class BotPlugin {
    private val eventManager = OneBot.eventManager

    private var config: YamlConfiguration? = null

    val configFile = File("config.conf")

    abstract fun onEnable()
    abstract fun onDisable()

    fun getConfig(): Configuration {
        if (config == null) {
            saveDefaultConfig()
        }

        return config!!
    }

    fun saveDefaultConfig() {
        if (!configFile.isFile) {
            val configResource: InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream("config.conf")
            if (configResource === null) {
                throw IllegalStateException("未找到resource下的config.conf文件")
            }

            val destinationPath: Path = configFile.toPath()
            Files.copy(configResource, destinationPath, StandardCopyOption.REPLACE_EXISTING)
        }

        if (config == null) config = YamlConfiguration(configFile)
    }

    fun saveConfig() {
        config?.let {
            if (!configFile.isFile) {
                configFile.createNewFile()
            }

            config!!.saveConfig(configFile)
        } ?: return

    }
}