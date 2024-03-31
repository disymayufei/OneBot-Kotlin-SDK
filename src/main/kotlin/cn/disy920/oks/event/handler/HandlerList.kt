package cn.disy920.oks.event.handler

import cn.disy920.oks.event.Event
import cn.disy920.oks.event.listener.Listener
import cn.disy920.oks.event.listener.WrappedListener

/**
 * 事件处理器列表，用于存储事件处理器
 * @param E 事件类型
 * @param eventClass 事件类型的类对象
 */
class HandlerList<E: Event>(private val eventClass: Class<E>) {
    private val handlerSlots = ArrayList<WrappedListener>()  // 事件监听器列表，按优先级排序

    var dirty = false  // 是否需要重新排序
        private set

    /**
     * 注册一个事件监听器
     * @param listener 事件监听器
     * @throws IllegalStateException 如果事件监听器已经注册过了
     * @see WrappedListener
     */
    @Synchronized
    fun register(listener: WrappedListener) {
        if (handlerSlots.contains(listener)) throw IllegalStateException("This listener is already registered to priority ${listener.priority}")
        handlerSlots.add(listener)
        markDirty()
    }

    /**
     * 注册一组包装的事件监听器
     * @param listeners 事件监听器列表
     * @see WrappedListener
     */
    @Synchronized
    fun registerAll(listeners: Collection<WrappedListener>) {
        handlerSlots.addAll(listeners)
        markDirty()
    }

    /**
     * 注销一个事件监听器
     * @param listener 事件监听器
     * @see WrappedListener
     */
    @Synchronized
    fun unregister(listener: WrappedListener) {
        handlerSlots.remove(listener)
    }

    /**
     * 注销一个事件监听器
     * @param listener 事件监听器
     * @see Listener
     */
    @Synchronized
    fun unregister(listener: Listener) {
        val iterator = handlerSlots.iterator()
        while (iterator.hasNext()) {
            val regListener = iterator.next()
            if (regListener.listener == listener) {
                iterator.remove()
            }
        }
    }

    /**
     * 注销已注册的所有事件监听器
     */
    @Synchronized
    fun unregisterAll() {
        handlerSlots.clear()
    }

    /**
     * 调用事件
     * @param event 事件
     * @see Event
     */
    @Synchronized
    fun callEvent(event: Event) {
        if (dirty) {
            handlerSlots.sortBy { it.priority }

            dirty = false
        }

        if (eventClass.isAssignableFrom(event::class.java)) {
            for (listener in handlerSlots) {
                listener.callEvent(event)
            }
        }
    }

    /**
     * 标记事件监听器列表需要重新排序
     */
    private fun markDirty() {
        this.dirty = true
    }
}