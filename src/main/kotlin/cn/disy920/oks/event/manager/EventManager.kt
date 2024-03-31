package cn.disy920.oks.event.manager

import cn.disy920.oks.annotation.Beta
import cn.disy920.oks.annotation.Undefined
import cn.disy920.oks.contact.Bot
import cn.disy920.oks.event.AbstractEvent
import cn.disy920.oks.event.BotEventHandler
import cn.disy920.oks.event.Event
import cn.disy920.oks.event.EventExecutor
import cn.disy920.oks.event.exception.EventException
import cn.disy920.oks.event.exception.IllegalListenerException
import cn.disy920.oks.event.handler.HandlerList
import cn.disy920.oks.event.listener.Listener
import cn.disy920.oks.event.listener.WrappedListener
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
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

    private val allEventHandlers = HashMap<Class<out Event>, MutableMap<Bot, HandlerList<out Event>>>()  // 事件处理器索引表

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
                    "Bot ${bot.id} has failed to register events for ${listener.javaClass} because ${e.message} does not exist."
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
                        logger.error("Bot ${bot.id} attempted to register more than 1 event in EventHandler method signature \"${method.toGenericString()}\" in ${listener.javaClass}")
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
                            logger.warn("Bot ${bot.id} attempted to listen the event ${eventClass.simpleName} which is deprecated!")
                        }

                        eventClass.getAnnotation(Beta::class.java)?.let {
                            logger.warn("Bot ${bot.id} attempted to listen the event ${eventClass.simpleName} which is in beta stage!")
                        }

                        eventClass.getAnnotation(Undefined::class.java)?.let {
                            logger.warn("Bot ${bot.id} attempted to listen the event ${eventClass.simpleName} which calling behavior is undefined!")
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
                    logger.error("Bot ${bot.id} attempted to register an invalid EventHandler method signature \"${method.toGenericString()}\" in ${listener.javaClass}")
                    continue
                }
            }

            return ret
        }
        else {
            throw IllegalListenerException("Bot ${bot.id} attempted to register an invalid event listener ${listener.javaClass} which is not the direct subclass of Listener.")
        }
    }

    /**
     * 注册事件监听器，请注意，注册的事件监听器仅会对来自当前机器人QQ的事件生效
     * @param listener 事件监听器
     * @param bot 机器人实例
     */
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
    fun callEvent(bot: Bot, event: AbstractEvent) {
        if (bot != event.bot) {
            throw EventException("Bot ${bot.id} attempted to call an event from another bot ${event.bot.id}")
        }

        allEventHandlers.entries.forEach {
            if (it.key.isAssignableFrom(event::class.java)) {
                it.value[bot]?.callEvent(event)
            }
        }
    }
}