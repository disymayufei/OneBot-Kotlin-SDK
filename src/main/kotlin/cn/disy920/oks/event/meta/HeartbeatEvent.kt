package cn.disy920.oks.event.meta

import cn.disy920.oks.contact.Bot
import cn.disy920.oks.event.meta.HeartbeatEvent.HeartbeatStatus

/**
 * 代表机器人的心跳事件，是元事件的种类之一，可用该事件确认与OneBot连接的存活状态
 * @param time 事件发生的时间戳
 * @param bot 事件对应的机器人
 * @param status 心跳状态
 * @param interval 心跳上报间隔
 * @see HeartbeatStatus
 * @see MetaEvent
 */
class HeartbeatEvent(
    override val time: Long,
    override val bot: Bot,
    val status: HeartbeatStatus,
    val interval: Long
) : MetaEvent(time, bot) {

    /**
     * 代表心跳状态
     * @param online 是否在线
     * @param good 是否正常
     * @see HeartbeatEvent
     */
    data class HeartbeatStatus(
        val online: Boolean,
        val good: Boolean
    ) {
        companion object {
            /**
             * 快速生成一个完全正常的心跳状态
             * @return 正常的心跳状态
             */
            fun okStatus(): HeartbeatStatus {
                return HeartbeatStatus(true, true)
            }
        }
    }
}
