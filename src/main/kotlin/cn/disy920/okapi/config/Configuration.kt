package cn.disy920.okapi.config

import java.io.File

abstract class Configuration : Section {
    protected abstract fun readConfig(configFile: File)
    protected abstract fun saveConfig(configFile: File)
}