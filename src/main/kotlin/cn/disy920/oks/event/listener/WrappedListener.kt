package cn.disy920.oks.event.listener

import cn.disy920.oks.event.Cancellable
import cn.disy920.oks.event.Event
import cn.disy920.oks.event.EventExecutor

/**
 * 一个包装了[Listener]的类，用于在事件触发时调用[Listener]的方法
 * @param listener 要包装的[Listener]
 * @param priority 优先级，数值越小，优先级越高
 * @param executor 事件执行器
 * @see Listener
 * @see EventExecutor
 */
class WrappedListener(
    val listener: Listener,
    val priority: Int,
    private val executor: EventExecutor
) {
    fun callEvent(event: Event) {
        if (
            event !is Cancellable ||
            !((event as Cancellable).cancelled)
        ) {
            this.executor.execute(this.listener, event)
        }
    }
}