package cn.disy920.oks.event.exception

/**
 * 当监听器不合法时抛出的异常
 */
class IllegalListenerException : RuntimeException {
    constructor()
    constructor(message: String) : super(message)
}