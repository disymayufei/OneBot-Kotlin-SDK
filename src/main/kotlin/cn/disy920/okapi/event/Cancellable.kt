package cn.disy920.okapi.event

/**
 * 代表一个可取消的事件
 */
interface Cancellable {
    var cancelled: Boolean
}