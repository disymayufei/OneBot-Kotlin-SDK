package cn.disy920.okapi.event.manager

import cn.disy920.okapi.annotation.Beta
import cn.disy920.okapi.annotation.Undefined
import cn.disy920.okapi.contact.Bot
import cn.disy920.okapi.event.AbstractEvent
import cn.disy920.okapi.event.BotEventHandler
import cn.disy920.okapi.event.Event
import cn.disy920.okapi.event.EventExecutor
import cn.disy920.okapi.event.exception.EventException
import cn.disy920.okapi.event.exception.IllegalListenerException
import cn.disy920.okapi.event.handler.HandlerList
import cn.disy920.okapi.event.listener.Listener
import cn.disy920.okapi.event.listener.WrappedListener
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

/**
 * 事件管理器，用于管理事件监听器
 * @see Listener
 */
class EventManager private constructor() {
    companion object {
        val INSTANCE = EventManager()
        private val logger = LoggerFactory.getLogger("OneBot Event Manager")
    }

    private val allEventHandlers = ConcurrentHashMap<Class<out Event>, MutableMap<Bot, HandlerList<out Event>>>()  // 事件处理器索引表

    /**
     * 将事件监听器包装成事件处理器
     * @param listener 事件监听器
     * @param bot 机器人实例
     * @return 事件处理器列表
     * @see WrappedListener
     * @see Listener
     */
    private fun warpListeners(
        listener: Listener,
        bot: Bot
    ): Map<Class<out Event>, Set<WrappedListener>> {
        val listenerClass = listener::class

        if (listenerClass.isSubclassOf(Listener::class) && listenerClass.superclasses.firstOrNull() == Listener::class) {
            val ret: MutableMap<Class<out Event>, MutableSet<WrappedListener>> = HashMap()
            val methods: MutableSet<Method>

            try {
                val publicMethods: Array<Method> = listener.javaClass.getMethods()
                val privateMethods: Array<Method> = listener.javaClass.getDeclaredMethods()

                methods = HashSet(publicMethods.size + privateMethods.size, 1.0f)

                methods.addAll(publicMethods)
                methods.addAll(privateMethods)
            }
            catch (e: NoClassDefFoundError) {
                logger.error(
                    "机器人${bot.id}在注册事件:${listener.javaClass}时由于${e.message}不存在而无法继续注册!"
                )

                return ret
            }

            for (method in methods) {
                val eventHandlerAnnotation = method.getAnnotation(BotEventHandler::class.java) ?: continue

                // 不要注册桥接或合成方法，以避免事件重复
                if (method.isBridge || method.isSynthetic) {
                    continue
                }

                if (method.parameterTypes.size == 1) {
                    val checkClass: Class<*> = method.parameterTypes[0]
                    if (!Event::class.java.isAssignableFrom(checkClass)) {
                        logger.error("机器人${bot.id}尝试注册的监听器${listener.javaClass}中包含异常方法：\"${method.toGenericString()}\"，它的参数类型不是一个合法的事件！")
                        continue
                    }
                    else {
                        val eventClass = checkClass.asSubclass(Event::class.java)
                        method.isAccessible = true
                        var eventSet: MutableSet<WrappedListener>? = ret[eventClass]
                        if (eventSet == null) {
                            eventSet = HashSet()
                            ret[eventClass] = eventSet
                        }

                        eventClass.getAnnotation(Deprecated::class.java)?.let {
                            logger.warn("机器人${bot.id}正在监听一个过时的事件：${eventClass.simpleName}，请联系开发者进行修改！")
                        }

                        eventClass.getAnnotation(Beta::class.java)?.let {
                            logger.warn("机器人${bot.id}正在监听一个处于测试状态的事件：${eventClass.simpleName}，这意味着该事件只能在特定条件下被触发，或其尚未经过足够的测试！")
                        }

                        eventClass.getAnnotation(Undefined::class.java)?.let {
                            logger.warn("机器人${bot.id}正在监听一个包含未定义行为的事件：${eventClass.simpleName}，这意味着该事件调用时的行为可能不会按照预期的情况发生！")
                        }

                        val executor = EventExecutor { eventListener, event ->
                            try {
                                if (!eventClass.isAssignableFrom(event::class.java)) {
                                    return@EventExecutor
                                }

                                method.invoke(eventListener, event)
                            }
                            catch (ex: InvocationTargetException) {
                                throw EventException(ex.cause)
                            }
                            catch (t: Throwable) {
                                throw EventException(t)
                            }
                        }

                        eventSet.add(
                            WrappedListener(
                                listener,
                                eventHandlerAnnotation.priority,
                                executor
                            )
                        )
                    }
                }
                else {
                    logger.error("机器人${bot.id}尝试注册的监听器${listener.javaClass}中包含一个异常方法: \"${method.toGenericString()}\"，该方法需要的参数数量不为1！")
                    continue
                }
            }

            return ret
        }
        else {
            throw IllegalListenerException("机器人${bot.id}尝试注册一个不是Listener接口的直接子类的监听器: ${listener.javaClass}")
        }
    }

    /**
     * 注册事件监听器，请注意，注册的事件监听器仅会对来自当前机器人QQ的事件生效
     * @param listener 事件监听器
     * @param bot 机器人实例
     */
    @Synchronized
    fun registerEvents(listener: Listener, bot: Bot) {
        val wrappedListeners = warpListeners(listener, bot)
        for (listenerEntry in wrappedListeners) {
            val handlerList : HandlerList<out Event> = allEventHandlers[listenerEntry.key]?.let { map ->
                map[bot] ?: let {
                    val newList = HandlerList(listenerEntry.key)
                    map[bot] = newList
                    newList
                }
            } ?: let {
                val newList = HandlerList(listenerEntry.key)

                val eventMap = HashMap<Bot, HandlerList<out Event>>()
                eventMap[bot] = newList
                allEventHandlers[listenerEntry.key] = eventMap

                newList
            }

            for (wrappedListener in listenerEntry.value) {
                handlerList.register(wrappedListener)
            }
        }
    }

    /**
     * 注销某机器人实例下的所有事件监听器
     * @param bot 机器人实例
     * @see Bot
     */
    @Synchronized
    fun unregisterEvents(bot: Bot) {
        for (handlerListMap in allEventHandlers.values) {
            handlerListMap.remove(bot)
        }
    }

    /**
     * 调用事件
     * @param bot 机器人实例
     * @param event 事件
     * @throws EventException 如果传入的机器人实例与事件发生的机器人不匹配
     * @see Bot
     */
    @Synchronized
    fun callEvent(bot: Bot, event: AbstractEvent) {
        if (bot != event.bot) {
            throw EventException("机器人${bot.id}尝试调用来自另一个机器人:${event.bot.id}的事件，这种行为是不被允许的！")
        }

        allEventHandlers.entries.forEach {
            if (it.key.isAssignableFrom(event::class.java)) {
                it.value[bot]?.callEvent(event)
            }
        }
    }
}