package cn.disy920.okapi.event.exception

/**
 * 事件异常
 * @param throwable 异常原因
 * @param message 异常信息
 */
class EventException : Exception {
    override val cause: Throwable?

    constructor(throwable: Throwable?) {
        cause = throwable
    }

    constructor() {
        cause = null
    }

    constructor(cause: Throwable, message: String) : super(message) {
        this.cause = cause
    }

    constructor(message: String) : super(message) {
        cause = null
    }
}