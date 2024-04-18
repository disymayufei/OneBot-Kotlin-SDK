package cn.disy920.okapi.event

import cn.disy920.okapi.event.manager.EventManager

/**
 * 用于标记一个方法为事件处理方法，被该方法标记的方法将会在注册监听器时，被注册到事件管理器中
 * @param priority 事件处理方法的优先级，数值越小，优先级越高
 * @see EventManager
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BotEventHandler(val priority: Int = 9999)
