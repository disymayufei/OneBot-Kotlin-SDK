package cn.disy920.okapi.utils

import java.lang.ref.WeakReference
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class Cache<T> {
    private val cache = ConcurrentHashMap<String, CacheData<T>>()

    companion object {
        /**
         * 默认过期时长，单位：秒
         */
        const val DEFAULT_EXPIRE: Long = 60 * 5  // 5min

        /**
         * 不设置过期时长
         */
        const val NOT_EXPIRE: Long = -1L
    }

    init {
        scheduleCleanup()
    }

    private fun scheduleCleanup() {
        val weakReference = WeakReference(this)
        val taskThread = Thread({
            while (true) {
                if (weakReference.get() == null) return@Thread
                deleteAllExpireElement()
                Thread.sleep(Duration.ofMinutes(5).toMillis())
            }
        }, "Cache Cleaner - @" + System.identityHashCode(this))

        taskThread.setDaemon(true)
        taskThread.start()
    }

    @Synchronized
    private fun deleteAllExpireElement(): Int {
        var count = 0

        val iterator = cache.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.isExpire()) {
                iterator.remove()
                ++count
            }
        }

        return count
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey 原key值
     * @param newKey 新key值
     */
    @Synchronized
    fun renameKey(oldKey: String, newKey: String) {
        if (cache.containsKey(oldKey)) {
            cache[newKey] = cache[oldKey]!!
            cache.remove(oldKey)
        }
    }

    /**
     * 重名名key，且仅在新key值不存在时才重命名
     *
     * @param oldKey 原key值
     * @param newKey 新key值
     * @return 修改成功返回true
     */
    @Synchronized
    fun renameKeyNotExist(oldKey: String, newKey: String): Boolean {
        if (cache.containsKey(oldKey)) {
            if (!cache.containsKey(newKey)) {
                cache[newKey] = cache[oldKey]!!
                cache.remove(oldKey)
                return true
            }
        }

        return false
    }


    /**
     * 删除key
     *
     * @param key 待删除的key值
     */
    @Synchronized
    fun deleteKey(key: String) {
        cache.remove(key)
    }

    /**
     * 删除多个key
     *
     * @param keys 待删除的一组key值
     */
    @Synchronized
    fun deleteKey(vararg keys: String) {
        keys.forEach { key: String -> cache.remove(key) }
    }

    /**
     * 删除Key的集合
     *
     * @param keys 待删除的key集合
     */
    @Synchronized
    fun deleteKey(keys: Collection<String>) {
        keys.forEach { key: String -> cache.remove(key) }
    }

    /**
     * 设置key的生命周期
     *
     * @param key 待设置的key值
     * @param expireTime 生命周期的时间值
     * @param expireTimeUnit 生命周期的时间单位
     */
    @Synchronized
    fun expireKey(key: String, expireTime: Long, expireTimeUnit: TimeUnit) {
        if (cache.containsKey(key)) {
            if (expireTime < 0) {
                cache[key]!!.neverExpire()
            } else {
                val value = cache[key]
                value!!.changeExpireTime(System.currentTimeMillis() + expireTimeUnit.toMillis(expireTime))
            }
        }
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key 待设置的key值
     * @param expireDate 过期的日期
     */
    @Synchronized
    fun expireKeyAt(key: String, expireDate: Date) {
        if (cache.containsKey(key)) {
            val value = cache[key]
            value!!.changeExpireTime(expireDate.time)
        }
    }

    /**
     * 查询key的生命周期
     *
     * @param key 待查询的key值
     * @return 对应时间单位的生命周期值
     */
    @Synchronized
    fun getKeyExpire(key: String): Long {
        return if (cache.containsKey(key)) {
            cache[key]!!.expireTime
        } else {
            0
        }
    }

    /**
     * 将key设置为永久有效
     *
     * @param key key值
     */
    @Synchronized
    fun persistKey(key: String) {
        cache[key]!!.neverExpire()
    }

    /**
     * 获取key对应的值并移除该键值对
     * @param key 待获取的key值
     * @return key对应的值
     */
    @Synchronized
    fun getAndRemove(key: String): T? {
        val value = this[key]
        deleteKey(key)
        return value
    }

    @Synchronized
    fun put(key: String, value: T, expireTime: Long, expireTimeUnit: TimeUnit) {
        val cacheData = CacheData(
            System.currentTimeMillis() + expireTimeUnit.toMillis(expireTime),
            value
        )
        cache[key] = cacheData
    }

    @Synchronized
    fun put(key: String, value: T, expireDuration: Duration) {
        val cacheData = CacheData(
            System.currentTimeMillis() + expireDuration.toMillis(),
            value
        )
        cache[key] = cacheData
    }

    @Synchronized
    operator fun set(key: String, value: T) {
        val cacheData = CacheData(
            System.currentTimeMillis() + DEFAULT_EXPIRE * 1000,
            value
        )
        cache[key] = cacheData
    }

    @Synchronized
    operator fun get(key: String): T? {
        val value = cache[key]
        if (value != null && value.isExpire()) {
            cache.remove(key)
            return null
        }

        return value?.value
    }

    @Synchronized
    private fun put(key: String, value: CacheData<T>) {
        cache[key] = value
    }

    inner class CacheData<T> (var expireTime: Long, val value: T) {
        fun isExpire(): Boolean {
            return expireTime != -1L && expireTime < System.currentTimeMillis()
        }

        fun neverExpire() {
            this.expireTime = -1
        }

        fun changeExpireTime(expireTime: Long) {
            this.expireTime = expireTime
        }


        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cache<*>.CacheData<*>

            if (expireTime != other.expireTime) return false
            if (value != other.value) return false

            return true
        }
    }
}