package cn.disy920.okapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class YamlConfiguration(
    private val configFile: File
) : Configuration() {
    private val data = ConcurrentHashMap<String, Any?>()

    companion object {
        @JvmStatic
        private val yamlMapper = ObjectMapper(YAMLFactory())
    }

    init {
        readConfig(configFile)
    }

    @Suppress("UNCHECKED_CAST")
    override fun readConfig(configFile: File) {
        if (!configFile.isFile) {
            return
        }

        val map = yamlMapper.readValue(configFile, Map::class.java) as Map<String, Any?>
        if (data.isNotEmpty()) {
            data.clear()
        }

        data.putAll(map)
    }

    @Synchronized
    public override fun saveConfig(configFile: File) {
        if (!configFile.isFile) {
            configFile.createNewFile()
        }

        yamlMapper.writeValue(configFile, data)
    }

    override fun get(key: String): Any? {
        return data[key]
    }

    @Suppress("UNCHECKED_CAST")
    override fun getSection(key: String): Section? {
        val sectionMap = data[key]
        if (sectionMap !is Map<*, *>) {
            return null
        }

        for (k in sectionMap.keys) {
            if (k !is String) {
                return null
            }
        }

        return YamlSection(sectionMap as Map<String, Any?>)
    }

    inner class YamlSection(data: Map<String, Any?>) : Section {
        private val sectionData = ConcurrentHashMap<String, Any?>()

        override fun get(key: String): Any? {
            return sectionData[key]
        }

        @Suppress("UNCHECKED_CAST")
        override fun getSection(key: String): Section? {
            val sectionMap = data[key]
            if (sectionMap !is Map<*, *>) {
                return null
            }

            for (k in sectionMap.keys) {
                if (k !is String) {
                    return null
                }
            }

            return YamlSection(sectionMap as Map<String, Any?>)
        }

        init {
            sectionData.putAll(data)
        }
    }
}