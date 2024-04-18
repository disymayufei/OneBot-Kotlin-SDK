package cn.disy920.okapi.config

interface Section {
    fun get(key: String): Any?
    fun getInt(key: String): Int {
        return getIntOrDefault(key, 0)
    }
    fun getIntOrDefault(key: String, default: Int): Int {
        return try {
            (get(key) ?: default) as Int
        }
        catch (e: ClassCastException) {
            default
        }
    }
    fun getShort(key: String): Short {
        return getShortOrDefault(key, 0)
    }
    fun getShortOrDefault(key: String, default: Short): Short {
        return try {
            (get(key) ?: default) as Short
        }
        catch (e: ClassCastException) {
            default
        }
    }

    fun getLong(key: String): Long {
        return getLongOrDefault(key, 0L)
    }

    fun getLongOrDefault(key: String, default: Long): Long {
        return try {
            (get(key) ?: default) as Long
        }
        catch (e: ClassCastException) {
            default
        }
    }

    fun getFloat(key: String): Float {
        return getFloatOrDefault(key, 0.0f)
    }

    fun getFloatOrDefault(key: String, default: Float): Float {
        return try {
            (get(key) ?: default) as Float
        }
        catch (e: ClassCastException) {
            default
        }
    }

    fun getDouble(key: String): Double {
        return getDoubleOrDefault(key, 0.0)
    }

    fun getDoubleOrDefault(key: String, default: Double): Double {
        return try {
            (get(key) ?: default) as Double
        }
        catch (e: ClassCastException) {
            default
        }
    }

    fun getBoolean(key: String): Boolean {
        return getBooleanOrDefault(key, false)
    }

    fun getBooleanOrDefault(key: String, default: Boolean): Boolean {
        return try {
            (get(key) ?: default) as Boolean
        }
        catch (e: ClassCastException) {
            default
        }
    }

    fun getString(key: String): String? {
        return try {
            get(key) as String
        }
        catch (e: ClassCastException) {
            null
        }
    }

    fun getStringOrDefault(key: String, default: String): String {
        return try {
            (get(key) ?: default) as String
        }
        catch (e: ClassCastException) {
            default
        }
    }

    fun getSection(key: String): Section?
}