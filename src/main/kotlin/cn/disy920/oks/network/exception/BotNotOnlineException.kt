package cn.disy920.oks.network.exception

/**
 * 当尝试操作一个不在线的Bot时会抛出此异常
 */
class BotNotOnlineException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}