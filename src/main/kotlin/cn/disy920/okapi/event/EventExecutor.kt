package cn.disy920.okapi.event

import cn.disy920.okapi.event.listener.Listener

/**
 * 事件执行器，用于在事件发生时执行具体的逻辑
 */
fun interface EventExecutor {
    fun execute(listener: Listener, event: Event)
}